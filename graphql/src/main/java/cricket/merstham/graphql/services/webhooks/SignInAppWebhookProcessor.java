package cricket.merstham.graphql.services.webhooks;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class SignInAppWebhookProcessor implements WebhookProcessor {
    private static final Logger LOG = LogManager.getLogger(SignInAppWebhookProcessor.class);
    private static final String NAME = "signinapp";
    public static final String WEBHOOK_SIGNATURE_HEADER = "x-signinapp-webhook-signature";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getId(JsonNode webhook) {
        return webhook.get("idempotency_key").asText();
    }

    @Override
    public boolean isValid(HttpHeaders httpHeaders, String body) {
        return true;
    }

    @Override
    public boolean processWebhook(JsonNode webhook) {
        return true;
    }
}
