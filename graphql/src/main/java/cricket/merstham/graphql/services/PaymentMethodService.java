package cricket.merstham.graphql.services;

import com.gocardless.GoCardlessClient;
import cricket.merstham.graphql.entity.UserPaymentMethodEntity;
import cricket.merstham.graphql.repository.UserPaymentMethodRepository;
import cricket.merstham.shared.dto.UserPaymentMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import static com.gocardless.GoCardlessClient.Environment.LIVE;
import static com.gocardless.GoCardlessClient.Environment.SANDBOX;

@Service
public class PaymentMethodService {

    private static final Logger LOG = LogManager.getLogger(PaymentMethodService.class);

    public static final String GOCARDLESS = "gocardless";
    public static final String MANDATE = "mandate";
    private final UserPaymentMethodRepository repository;
    private final ModelMapper modelMapper;
    private final CognitoService cognitoService;
    private final String accessToken;
    private final boolean sandbox;

    @Autowired
    public PaymentMethodService(
            UserPaymentMethodRepository repository,
            ModelMapper modelMapper,
            CognitoService cognitoService,
            @Value("${configuration.webhooks.gocardless.access-token}") String accessToken,
            @Value("${configuration.webhooks.gocardless.sandbox}") boolean sandbox) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.cognitoService = cognitoService;
        this.accessToken = accessToken;
        this.sandbox = sandbox;
    }

    @PreAuthorize("isAuthenticated()")
    public List<UserPaymentMethod> getPaymentMethods(String userId) {
        return repository.findAllByUserId(userId).stream()
                .map(p -> modelMapper.map(p, UserPaymentMethod.class))
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    public UserPaymentMethod savePaymentMethod(UserPaymentMethod paymentMethod) {
        var entity = UserPaymentMethodEntity.builder().build();
        modelMapper.map(entity, paymentMethod);

        return modelMapper.map(repository.saveAndFlush(entity), UserPaymentMethod.class);
    }

    public int importPaymentMethodsFromGoCardless() {
        AtomicInteger result = new AtomicInteger();
        var client =
                GoCardlessClient.newBuilder(accessToken)
                        .withEnvironment(sandbox ? SANDBOX : LIVE)
                        .build();

        var results =
                client.mandates()
                        .list()
                        .withCreatedAtGte(
                                LocalDateTime.of(2024, 01, 01, 0, 0)
                                                .format(DateTimeFormatter.ISO_DATE_TIME)
                                        + "Z")
                        .execute();

        results.getItems()
                .forEach(
                        mandate -> {
                            var customer =
                                    client.customers()
                                            .get(mandate.getLinks().getCustomer())
                                            .execute();
                            try {
                                var userId =
                                        cognitoService
                                                .getUserDetails(customer.getEmail())
                                                .getSubjectId();
                                var entity =
                                        repository
                                                .findByUserIdAndProviderAndTypeAndMethodIdentifier(
                                                        userId,
                                                        GOCARDLESS,
                                                        MANDATE,
                                                        mandate.getId())
                                                .orElseGet(
                                                        () ->
                                                                UserPaymentMethodEntity.builder()
                                                                        .userId(userId)
                                                                        .provider(GOCARDLESS)
                                                                        .type(MANDATE)
                                                                        .methodIdentifier(
                                                                                mandate.getId())
                                                                        .build());

                                repository.saveAndFlush(
                                        entity.setCustomerIdentifier(customer.getId())
                                                .setCreateDate(
                                                        LocalDateTime.parse(
                                                                mandate.getCreatedAt(),
                                                                DateTimeFormatter.ISO_DATE_TIME))
                                                .setStatus(
                                                        mandate.getStatus()
                                                                .toString()
                                                                .toLowerCase(Locale.ROOT)));
                                result.getAndIncrement();
                            } catch (Exception e) {
                                LOG.atWarn()
                                        .withThrowable(e)
                                        .log("Could not persist mandate {}", mandate.getId());
                            }
                        });
        return result.get();
    }
}
