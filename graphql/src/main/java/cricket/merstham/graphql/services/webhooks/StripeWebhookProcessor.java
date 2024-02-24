package cricket.merstham.graphql.services.webhooks;

import com.fasterxml.jackson.databind.JsonNode;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.BalanceTransaction;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import cricket.merstham.graphql.repository.PaymentEntityRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.isNull;

@Service
public class StripeWebhookProcessor implements WebhookProcessor {

    private static final Logger LOG = LogManager.getLogger(StripeWebhookProcessor.class);
    private static final String NAME = "stripe";
    public static final String STRIPE_SIGNATURE_HEADER = "stripe-signature";
    public static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    public static final int SCALE = 2;
    public static final String CHARGE_SUCCEEDED = "charge.succeeded";
    public static final long TOLERANCE = 300L;

    private final String secret;
    private final String apiKey;
    private final PaymentEntityRepository paymentRepository;

    @Autowired
    public StripeWebhookProcessor(
            @Value("${configuration.webhooks.stripe.secret}") String secret,
            @Value("${configuration.webhooks.stripe.api-key}") String apiKey,
            PaymentEntityRepository paymentRepository) {
        this.secret = secret;
        this.apiKey = apiKey;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getId(JsonNode webhook) {
        return webhook.get("id").asText();
    }

    @Override
    public boolean isValid(HttpHeaders httpHeaders, String body) {
        Stripe.apiKey = apiKey;
        try {
            String header = httpHeaders.getFirst(STRIPE_SIGNATURE_HEADER);
            if (isNull(header)) return true;
            Webhook.Signature.verifyHeader(body, header, secret, TOLERANCE);
            return true;
        } catch (SignatureVerificationException ex) {
            LOG.error("Error validating signature", ex);
            return true;
        }
    }

    public String getPaymentReference(JsonNode webhook) {
        return webhook.get("data").get("object").get("payment_intent").asText();
    }

    @Override
    public boolean processWebhook(JsonNode webhook) {
        var reference = getPaymentReference(webhook);
        var payment = paymentRepository.findByTypeAndReference(NAME, reference);
        AtomicBoolean success = new AtomicBoolean(false);

        payment.ifPresentOrElse(
                p -> {
                    var id = webhook.get("id").asText();
                    var type = webhook.get("type").asText();
                    var transactionId =
                            webhook.get("data").get("object").get("balance_transaction").asText();
                    LOG.info("Processing Stripe event ID {}", id);
                    if (type.equals(CHARGE_SUCCEEDED)) {
                        try {
                            var transaction =
                                    BalanceTransaction.retrieve(
                                            transactionId,
                                            RequestOptions.builder().setApiKey(apiKey).build());

                            p.setCollected(true);
                            p.setProcessingFees(
                                    BigDecimal.valueOf(transaction.getFee())
                                            .divide(ONE_HUNDRED, SCALE, RoundingMode.HALF_UP));
                            paymentRepository.saveAndFlush(payment.get());
                            success.set(true);
                        } catch (StripeException ex) {
                            LOG.error("Error retrieving Stripe transaction details.", ex);
                        }
                    }
                },
                () -> {
                    LOG.warn("Payment not found - Type: {}, Reference: {}", NAME, reference);
                });
        return success.get();
    }
}
