package cricket.merstham.graphql.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import cricket.merstham.graphql.dto.WebhookResult;
import cricket.merstham.graphql.services.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WebhookController {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookController.class);
    private static final List<String> VALID_TYPES = List.of("gocardless", "paypal", "stripe");

    private final WebhookService service;

    @Autowired
    public WebhookController(WebhookService service) {
        this.service = service;
    }

    @PostMapping(
            value = "/webhooks/{type}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebhookResult> receiveWebhook(
            @PathVariable("type") String type,
            @RequestHeader HttpHeaders httpHeaders,
            @RequestBody JsonNode body) {
        if (VALID_TYPES.contains(type)) {
            var headers = httpHeaders.toSingleValueMap();
            try {
                var result = service.recordWebhook(type, body, headers);

                return ResponseEntity.ok()
                        .body(
                                WebhookResult.builder()
                                        .id(result.getId())
                                        .reference(result.getReference())
                                        .status(200)
                                        .message("Webhook Received")
                                        .build());
            } catch (Exception ex) {
                LOG.error("Error processing webhook", ex);
                return ResponseEntity.internalServerError()
                        .body(
                                WebhookResult.builder()
                                        .status(500)
                                        .message("Error processing webhook")
                                        .build());
            }
        }
        LOG.error("Invalid webhook type {}", type);
        return ResponseEntity.status(404)
                .body(WebhookResult.builder().status(404).message("Not found").build());
    }
}
