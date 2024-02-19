package cricket.merstham.graphql.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cricket.merstham.graphql.repository.OrderEntityRepository;
import cricket.merstham.graphql.repository.PaymentEntityRepository;
import cricket.merstham.shared.dto.Order;
import io.micrometer.core.annotation.Timed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class AccountingService {
    private static final Logger LOG = LogManager.getLogger(AccountingService.class);
    public static final String ID = "id";
    public static final String UUID = "uuid";
    public static final String REFERENCE = "reference";
    public static final String DATE = "date";
    public static final String OWNER = "owner";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String CATEGORY = "category";
    public static final String PRICE = "price";
    public static final String YEAR = "year";
    public static final String LINES = "lines";
    public static final String DISCOUNT = "discount";
    public static final String TOTAL = "total";

    private final OrderEntityRepository repository;
    private final PaymentEntityRepository paymentRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final String createSalesOrderArn;
    private final LambdaClient client;

    @Autowired
    public AccountingService(
            OrderEntityRepository repository,
            PaymentEntityRepository paymentRepository,
            ModelMapper modelMapper,
            ObjectMapper objectMapper,
            @Value("${configuration.accounting.create-sales-order-arn}") String createSalesOrderArn,
            LambdaClient client) {
        this.repository = repository;
        this.paymentRepository = paymentRepository;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        this.createSalesOrderArn = createSalesOrderArn;
        this.client = client;
    }

    @Scheduled(
            cron = "${configuration.accounting.sync-cron}",
            zone = "${configuration.scheduler-zone}")
    @Timed(
            value = "accounting.sales-order.sync",
            description = "Time taken to sync sales orders with accounts")
    @Transactional(propagation = Propagation.REQUIRED)
    public void accountingSalesOrderSync() {
        LOG.info("Starting accounting sync...");
        var orders = repository.findOrderEntitiesByAccountingIdIsNull();
        orders.forEach(
                order -> {
                    if (!order.getMemberSubscription().isEmpty()) {
                        order.setAccountingId(
                                sendToAccounting(orderJson(modelMapper.map(order, Order.class))));
                    }
                });
        repository.saveAllAndFlush(orders);
        LOG.info("Finished accounting sync!");
    }

    private String sendToAccounting(JsonNode order) {
        try {
            if (!(isNull(createSalesOrderArn) || createSalesOrderArn.isBlank())) {
                var response =
                        client.invoke(
                                InvokeRequest.builder()
                                        .functionName(createSalesOrderArn)
                                        .payload(
                                                SdkBytes.fromByteArray(
                                                        objectMapper.writeValueAsBytes(order)))
                                        .invocationType(InvocationType.REQUEST_RESPONSE)
                                        .build());
                var result = objectMapper.readTree(response.payload().asByteArray());
                LOG.info("Lambda response: {}", result);
                if (nonNull(response.functionError()) && !response.functionError().isBlank()) {
                    LOG.error("Function error: {}", response.functionError());
                    var error = result.get("errorMessage");
                    throw new RuntimeException(
                            isNull(error)
                                    ? "Unknown error from accounting service"
                                    : error.asText());
                }
                return result.get(ID).asText();
            } else {
                LOG.info("Order JSON: {}", order);
                return null;
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error processing order", ex);
        }
    }

    private JsonNode orderJson(Order order) {
        var root = objectMapper.createObjectNode();
        root.put(ID, order.getId());
        root.put(UUID, order.getUuid());
        root.put(REFERENCE, order.getWebReference());
        root.put(DATE, order.getCreateDate().format(DateTimeFormatter.ISO_DATE));
        root.put(OWNER, order.getOwnerUserId());
        var lines = objectMapper.createArrayNode();
        AtomicReference<BigDecimal> runningTotal = new AtomicReference<>(BigDecimal.ZERO);
        order.getMemberSubscription()
                .forEach(
                        sub -> {
                            var line = objectMapper.createObjectNode();
                            line.put(
                                    FIRST_NAME,
                                    sub.getMember().getAttributeMap().get("given-name").asText());
                            line.put(
                                    LAST_NAME,
                                    sub.getMember().getAttributeMap().get("family-name").asText());
                            line.put(CATEGORY, sub.getPriceListItem().getMemberCategory().getKey());
                            line.put(PRICE, sub.getPrice());
                            line.put(YEAR, sub.getYear());
                            runningTotal.set(runningTotal.get().add(sub.getPrice()));

                            lines.add(line);
                        });
        root.putArray(LINES).addAll(lines);

        if (!order.getPayment().isEmpty()) {
            AtomicReference<BigDecimal> totalPayments = new AtomicReference<>(BigDecimal.ZERO);
            order.getPayment()
                    .forEach(
                            payment -> {
                                totalPayments.set(totalPayments.get().add(payment.getAmount()));
                            });
            if (!totalPayments.equals(runningTotal)) {
                root.put(DISCOUNT, totalPayments.get().subtract(runningTotal.get()));
            }
        }
        root.put(TOTAL, runningTotal.get());
        return root;
    }
}
