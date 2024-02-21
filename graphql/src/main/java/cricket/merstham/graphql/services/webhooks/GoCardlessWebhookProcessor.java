package cricket.merstham.graphql.services.webhooks;

import com.fasterxml.jackson.databind.JsonNode;
import cricket.merstham.graphql.entity.PaymentEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class GoCardlessWebhookProcessor implements WebhookProcessor {

    private static final Logger LOG = LogManager.getLogger(GoCardlessWebhookProcessor.class);
    private static final String NAME = "gocardless";

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
        return true;
    }

    @Override
    public String getPaymentReference(JsonNode webhook) {
        return null;
    }

    @Override
    public boolean processWebhook(JsonNode webhook, PaymentEntity payment) {
        return false;
    }
}
