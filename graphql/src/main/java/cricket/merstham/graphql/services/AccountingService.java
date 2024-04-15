package cricket.merstham.graphql.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cricket.merstham.graphql.entity.PaymentEntity;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;
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
    private final OrderEntityRepository orderRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final String createSalesOrderArn;
    private final String createPaymentArn;
    private final String getPaymentsArn;
    private final LambdaClient client;

    @Autowired
    public AccountingService(
            OrderEntityRepository repository,
            PaymentEntityRepository paymentRepository,
            OrderEntityRepository orderRepository,
            ModelMapper modelMapper,
            ObjectMapper objectMapper,
            @Value("${configuration.accounting.create-sales-order-arn}") String createSalesOrderArn,
            @Value("${configuration.accounting.create-payment-arn}") String createPaymentArn,
            @Value("${configuration.accounting.get-payments-arn}") String getPaymentsArn,
            LambdaClient client) {
        this.repository = repository;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        this.createSalesOrderArn = createSalesOrderArn;
        this.createPaymentArn = createPaymentArn;
        this.getPaymentsArn = getPaymentsArn;
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
        try {
            LOG.info("Send new orders...");
            var orders = repository.findOrderEntitiesByAccountingIdIsNullAndAccountingErrorIsNull();
            orders.forEach(
                    order -> {
                        if (!order.getMemberSubscription().isEmpty()) {
                            try {
                                order.setAccountingId(
                                        sendOrderToAccounting(
                                                orderJson(modelMapper.map(order, Order.class))));
                            } catch (Exception ex) {
                                LOG.error(
                                        "Error processing order {}: ",
                                        order.getId(),
                                        ex.getMessage());
                                order.setAccountingError(
                                        isNull(ex.getCause())
                                                ? ex.getMessage()
                                                : ex.getCause().getMessage());
                            }
                        }
                    });
            repository.saveAllAndFlush(orders);
            LOG.info("Order sync complete!");
        } catch (Exception ex) {
            LOG.error("Error sending orders to accounting", ex);
        }

        try {
            LOG.info("Send payments...");
            var payments =
                    paymentRepository
                            .findPaymentEntitiesByReconciledIsFalseAndCollectedIsTrueAndAccountingErrorIsNull();
            payments.forEach(
                    payment -> {
                        try {
                            var result = sendPaymentToAccounting(payment);
                            payment.setAccountingId(result.get("payment_id").asText());
                            payment.setFeesAccountingId(result.get("fee_id").asText());
                            payment.setReconciled(true);
                        } catch (Exception ex) {
                            LOG.error(
                                    "Error processing payment {}: ",
                                    payment.getId(),
                                    ex.getMessage());
                            payment.setAccountingError(
                                    isNull(ex.getCause())
                                            ? ex.getMessage()
                                            : ex.getCause().getMessage());
                        }
                    });
            paymentRepository.saveAllAndFlush(payments);
        } catch (Exception ex) {
            LOG.error("Error sending payments to accounting", ex);
        }

        try {
            LOG.info("Receiving offline payments...");
            var payments = syncOfflinePayments();
            paymentRepository.saveAllAndFlush(payments);
            LOG.info("Payment sync complete!");
        } catch (Exception ex) {
            LOG.error("Error receiving payments to accounting", ex);
        }
        LOG.info("Finished accounting sync!");
    }

    private List<PaymentEntity> syncOfflinePayments() {
        var since = Instant.now().minus(24, ChronoUnit.HOURS);
        Map<String, Object> request = Map.of("since", since);
        List<PaymentEntity> result = new ArrayList<>();
        try {
            var response =
                    client.invoke(
                            InvokeRequest.builder()
                                    .functionName(getPaymentsArn)
                                    .payload(
                                            SdkBytes.fromByteArray(
                                                    objectMapper.writeValueAsBytes(request)))
                                    .invocationType(InvocationType.REQUEST_RESPONSE)
                                    .build());
            var payments = objectMapper.readTree(response.payload().asByteArray());
            LOG.info("Found {} offline payments to process!", payments.size());
            payments.forEach(
                    p -> {
                        var id = p.get("id").asText();
                        LOG.info("Processing accounting payment id {}", id);
                        var entities = paymentRepository.findByAccountingId(id);
                        if (entities.isEmpty()) {
                            p.get("artefact")
                                    .forEach(
                                            a -> {
                                                var order =
                                                        orderRepository
                                                                .findOrderEntityByAccountingId(
                                                                        a.get("id").asText());

                                                order.ifPresentOrElse(
                                                        o ->
                                                                result.add(
                                                                        PaymentEntity.builder()
                                                                                .accountingId(id)
                                                                                .date(
                                                                                        LocalDate
                                                                                                .parse(
                                                                                                        p.get(
                                                                                                                        "date")
                                                                                                                .asText()))
                                                                                .reference(
                                                                                        p.get(
                                                                                                        "reference")
                                                                                                .asText())
                                                                                .type("bank")
                                                                                .order(o)
                                                                                .amount(
                                                                                        BigDecimal
                                                                                                .valueOf(
                                                                                                        a.get(
                                                                                                                        "amount")
                                                                                                                .asDouble()))
                                                                                .feesAccountingId(
                                                                                        null)
                                                                                .processingFees(
                                                                                        BigDecimal
                                                                                                .ZERO)
                                                                                .reconciled(true)
                                                                                .collected(true)
                                                                                .build()),
                                                        () ->
                                                                LOG.warn(
                                                                        "Error could not be found during sync {}",
                                                                        a.get("displayedAs")
                                                                                .asText()));
                                            });
                        }
                    });
            return result;
        } catch (Exception ex) {
            throw new RuntimeException("Error getting offline payments", ex);
        }
    }

    private JsonNode sendPaymentToAccounting(PaymentEntity payment) {
        Map<String, Object> request =
                Map.of(
                        "id", payment.getId(),
                        "type", payment.getType(),
                        "reference", payment.getReference(),
                        "date", payment.getDate().format(DateTimeFormatter.ISO_DATE),
                        "amount", payment.getAmount(),
                        "fees", payment.getProcessingFees(),
                        "order_id", payment.getOrder().getAccountingId());
        try {
            if (!(isNull(createPaymentArn) || createPaymentArn.isBlank())) {
                var response =
                        client.invoke(
                                InvokeRequest.builder()
                                        .functionName(createPaymentArn)
                                        .payload(
                                                SdkBytes.fromByteArray(
                                                        objectMapper.writeValueAsBytes(request)))
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
                return result;
            } else {
                LOG.info("Request JSON: {}", request);
                return null;
            }
        } catch (Exception ex) {
            throw new RuntimeException(
                    format("Error processing payment: {0}", payment.getId()), ex);
        }
    }

    private String sendOrderToAccounting(JsonNode order) {
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

                            lines.add(line);
                        });
        if (order.getDiscount().compareTo(BigDecimal.ZERO) != 0) {
            root.put(DISCOUNT, order.getDiscount().negate());
        }
        root.putArray(LINES).addAll(lines);

        root.put(TOTAL, order.getTotal());
        return root;
    }
}
