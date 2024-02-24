package cricket.merstham.graphql.services.webhooks;

import com.fasterxml.jackson.databind.JsonNode;
import com.gocardless.GoCardlessClient;
import com.gocardless.Webhook;
import com.gocardless.http.WebhookParser;
import com.gocardless.resources.Event;
import com.gocardless.resources.PayoutItem;
import cricket.merstham.graphql.repository.PaymentEntityRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.gocardless.GoCardlessClient.Environment.LIVE;
import static com.gocardless.GoCardlessClient.Environment.SANDBOX;
import static com.gocardless.resources.Event.ResourceType.PAYOUTS;
import static java.util.Objects.isNull;

@Service
public class GoCardlessWebhookProcessor implements WebhookProcessor {

    private static final Logger LOG = LogManager.getLogger(GoCardlessWebhookProcessor.class);
    private static final String NAME = "gocardless";
    public static final String WEBHOOK_SIGNATURE_HEADER = "webhook-signature";

    private final String secret;
    private final GoCardlessClient client;
    private final PaymentEntityRepository paymentEntityRepository;

    public GoCardlessWebhookProcessor(
            @Value("${configuration.webhooks.gocardless.secret}") String secret,
            @Value("${configuration.webhooks.gocardless.access-token}") String accessToken,
            @Value("${configuration.webhooks.gocardless.sandbox}") boolean sandbox,
            PaymentEntityRepository paymentEntityRepository) {
        this.secret = secret;
        this.client =
                GoCardlessClient.newBuilder(accessToken)
                        .withEnvironment(sandbox ? SANDBOX : LIVE)
                        .build();
        this.paymentEntityRepository = paymentEntityRepository;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getId(JsonNode webhook) {
        return webhook.get("meta").get("webhook_id").asText();
    }

    @Override
    public boolean isValid(HttpHeaders httpHeaders, String body) {
        var header = httpHeaders.getFirst(WEBHOOK_SIGNATURE_HEADER);
        if (isNull(header)) return true;
        if (Webhook.isValidSignature(body, header, secret)) {
            LOG.warn("Signature validation failed on GoCardless webhook: {}", body);
        }
        return true;
    }

    @Override
    public boolean processWebhook(JsonNode webhook) {
        AtomicBoolean success = new AtomicBoolean(true);
        try {
            List<Event> events = WebhookParser.parse(webhook.toPrettyString());
            LOG.info("Processing GoCardless Payouts...");
            events.stream().filter(this::isPayoutEvent).forEach(this::processPayout);
        } catch (Exception ex) {
            LOG.error("Unexpected error processing GoCardless webhook", ex);
            success.set(false);
        }
        return success.get();
    }

    private void processPayout(Event event) {
        var link = event.getLinks().getPayout();

        LOG.info("Processing GoCardless payout event: {}", link);
        var payoutItems = client.payoutItems().list().withPayout(link).withLimit(500).execute();

        payoutItems.getItems().stream()
                .filter(p -> p.getType() == PayoutItem.Type.GOCARDLESS_FEE)
                .forEach(
                        payoutItem -> {
                            var paymentId = payoutItem.getLinks().getPayment();
                            var payment =
                                    paymentEntityRepository.findByTypeAndReference(NAME, paymentId);

                            payment.ifPresentOrElse(
                                    p -> {
                                        var fee = convertAmount(payoutItem.getAmount());

                                        p.setProcessingFees(fee);
                                        p.setCollected(true);
                                        paymentEntityRepository.saveAndFlush(p);
                                    },
                                    () ->
                                            LOG.warn(
                                                    "Could not find payment {} specified in payout {}",
                                                    paymentId,
                                                    link));
                            LOG.info("Payment updated and saved: {}", paymentId);
                        });
    }

    public BigDecimal convertAmount(String amount) {
        return BigDecimal.valueOf(Double.parseDouble(amount))
                .setScale(2, RoundingMode.UNNECESSARY)
                .divide(BigDecimal.valueOf(100), RoundingMode.UNNECESSARY);
    }

    private boolean isPayoutEvent(Event event) {
        return event.getResourceType().equals(PAYOUTS) && event.getAction().equals("paid");
    }
}
