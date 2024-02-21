package cricket.merstham.graphql.services.webhooks;

import com.fasterxml.jackson.databind.JsonNode;
import cricket.merstham.graphql.entity.PaymentEntity;
import org.springframework.http.HttpHeaders;

public interface WebhookProcessor {
    String getName();

    String getId(JsonNode webhook);

    boolean isValid(HttpHeaders httpHeaders, String body);

    String getPaymentReference(JsonNode webhook);

    boolean processWebhook(JsonNode webhook, PaymentEntity payment);
}
