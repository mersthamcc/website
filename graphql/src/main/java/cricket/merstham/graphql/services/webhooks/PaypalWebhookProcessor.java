package cricket.merstham.graphql.services.webhooks;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class PaypalWebhookProcessor implements WebhookProcessor {
    private static final Logger LOG = LogManager.getLogger(PaypalWebhookProcessor.class);
    private static final String NAME = "paypal";

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
        return true;
    }

    public String getPaymentReference(JsonNode webhook) {
        return null;
    }

    @Override
    public boolean processWebhook(JsonNode webhook) {
        return false;
    }
}
