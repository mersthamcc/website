package cricket.merstham.graphql.services.webhooks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpRequest;
import com.paypal.http.annotations.Model;
import com.paypal.http.annotations.SerializedName;
import cricket.merstham.graphql.repository.PaymentEntityRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.paypal.http.Headers.CONTENT_TYPE;
import static jakarta.ws.rs.HttpMethod.POST;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Service
public class PaypalWebhookProcessor implements WebhookProcessor {
    private static final Logger LOG = LogManager.getLogger(PaypalWebhookProcessor.class);
    private static final String NAME = "paypal";
    private static final String PAYMENT_COMPLETED = "PAYMENT.CAPTURE.COMPLETED";
    public static final String VERIFY_WEBHOOK_SIGNATURE_PATH =
            "/v1/notifications/verify-webhook-signature";
    public static final String EVENT_TYPE = "event_type";
    public static final String RESOURCE = "resource";
    public static final String SELLER_RECEIVABLE_BREAKDOWN = "seller_receivable_breakdown";
    public static final String PAYPAL_FEE = "paypal_fee";
    public static final String VALUE = "value";
    public static final String ID = "id";
    public static final String PAYPAL_AUTH_ALGO_HEADER = "paypal-auth-algo";
    public static final String PAYPAL_CERT_URL_HEADER = "paypal-cert-url";
    public static final String PAYPAL_TRANSMISSION_ID_HEADER = "paypal-transmission-id";
    public static final String PAYPAL_TRANSMISSION_SIG_HEADER = "paypal-transmission-sig";
    public static final String PAYPAL_TRANSMISSION_TIME_HEADER = "paypal-transmission-time";

    private final PaymentEntityRepository paymentRepository;
    private final PayPalHttpClient client;
    private final ObjectMapper objectMapper;
    private final String webhookId;

    @Autowired
    public PaypalWebhookProcessor(
            PaymentEntityRepository paymentRepository,
            ObjectMapper objectMapper,
            @Value("${configuration.webhooks.paypal.client-id}") String clientId,
            @Value("${configuration.webhooks.paypal.client-secret}") String clientSecret,
            @Value("${configuration.webhooks.paypal.sandbox}") boolean sandbox,
            @Value("${configuration.webhooks.paypal.webhook-id}") String webhookId) {
        this.paymentRepository = paymentRepository;
        this.objectMapper = objectMapper;
        this.webhookId = webhookId;
        PayPalEnvironment environment;
        if (sandbox) {
            environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
        } else {
            environment = new PayPalEnvironment.Live(clientId, clientSecret);
        }
        this.client = new PayPalHttpClient(environment);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getId(JsonNode webhook) {
        return webhook.get(ID).asText();
    }

    @Override
    public boolean isValid(HttpHeaders httpHeaders, String body) {
        try {
            var result =
                    client.execute(
                            new HttpRequest<>(
                                            VERIFY_WEBHOOK_SIGNATURE_PATH,
                                            POST,
                                            VerificationResponse.class)
                                    .requestBody(createVerificationRequest(httpHeaders, body))
                                    .header(CONTENT_TYPE, APPLICATION_JSON));
            LOG.info(
                    "PayPal verification result status code {}, result = {}",
                    result.statusCode(),
                    result.result().getVerificationStatus());
        } catch (Exception ex) {
            LOG.error("Error calling PayPal verification", ex);
        }
        return true;
    }

    @Override
    public boolean processWebhook(JsonNode webhook) {
        var reference = getPaymentReference(webhook);
        var payment = paymentRepository.findByTypeAndReference(NAME, reference);
        AtomicBoolean success = new AtomicBoolean(false);

        payment.ifPresentOrElse(
                p -> {
                    var type = webhook.get(EVENT_TYPE).asText();
                    LOG.info("Processing Paypal event ID {}", getId(webhook));
                    if (type.equals(PAYMENT_COMPLETED)) {
                        p.setCollected(true);
                        p.setProcessingFees(
                                BigDecimal.valueOf(
                                        Double.parseDouble(
                                                webhook.get(RESOURCE)
                                                        .get(SELLER_RECEIVABLE_BREAKDOWN)
                                                        .get(PAYPAL_FEE)
                                                        .get(VALUE)
                                                        .asText())));
                        paymentRepository.saveAndFlush(payment.get());
                        success.set(true);
                    }
                },
                () -> {
                    LOG.warn("Payment not found - Type: {}, Reference: {}", NAME, reference);
                });
        return success.get();
    }

    private String getPaymentReference(JsonNode webhook) {
        return webhook.get(RESOURCE).get(ID).asText();
    }

    private VerificationRequest createVerificationRequest(HttpHeaders httpHeaders, String body)
            throws JsonProcessingException {
        return VerificationRequest.builder()
                .authAlgo(httpHeaders.getFirst(PAYPAL_AUTH_ALGO_HEADER))
                .certUrl(httpHeaders.getFirst(PAYPAL_CERT_URL_HEADER))
                .transmissionId(httpHeaders.getFirst(PAYPAL_TRANSMISSION_ID_HEADER))
                .transmissionSig(httpHeaders.getFirst(PAYPAL_TRANSMISSION_SIG_HEADER))
                .transmissionTime(httpHeaders.getFirst(PAYPAL_TRANSMISSION_TIME_HEADER))
                .webhookId(webhookId)
                .webhookEvent(objectMapper.readTree(body))
                .build();
    }

    @Data
    @Model
    @Builder
    public static class VerificationRequest {
        @SerializedName("auth_algo")
        private String authAlgo;

        @SerializedName("cert_url")
        private String certUrl;

        @SerializedName("transmission_id")
        private String transmissionId;

        @SerializedName("transmission_sig")
        private String transmissionSig;

        @SerializedName("transmission_time")
        private String transmissionTime;

        @SerializedName("webhook_id")
        private String webhookId;

        @SerializedName("webhook_event")
        private Object webhookEvent;
    }

    @Data
    @Model
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VerificationResponse {
        private static final String SUCCESS = "SUCCESS";

        @SerializedName("verification_status")
        private String verificationStatus;
    }
}
