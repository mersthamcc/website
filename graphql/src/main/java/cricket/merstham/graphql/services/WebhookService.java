package cricket.merstham.graphql.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cricket.merstham.graphql.entity.WebhookReceivedEntity;
import cricket.merstham.graphql.repository.WebhookReceivedRepository;
import cricket.merstham.shared.dto.WebhookReceived;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class WebhookService {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookService.class);

    private final WebhookReceivedRepository repository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public WebhookService(
            WebhookReceivedRepository repository,
            ModelMapper modelMapper,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    public WebhookReceived recordWebhook(String type, JsonNode body, Map<String, String> headers) {
        var serialisedHeaders = objectMapper.valueToTree(headers);
        var entity =
                repository.save(
                        WebhookReceivedEntity.builder()
                                .receivedDate(Instant.now())
                                .type(type)
                                .reference(extractId(type, body))
                                .processed(false)
                                .headers(serialisedHeaders)
                                .body(body)
                                .build());

        return modelMapper.map(entity, WebhookReceived.class);
    }

    private String extractId(String type, JsonNode body) {
        if (type.equals("gocardless")) {
            return body.get("meta").get("webhook_id").asText();
        }
        return body.get("id").asText();
    }
}
