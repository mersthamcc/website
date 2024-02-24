package cricket.merstham.graphql.services.webhooks;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpHeaders;

public interface WebhookProcessor {
    String getName();

    String getId(JsonNode webhook);

    boolean isValid(HttpHeaders httpHeaders, String body);

    boolean processWebhook(JsonNode webhook);
}
