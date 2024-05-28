package cricket.merstham.graphql.controllers;

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

import static cricket.merstham.shared.helpers.InputSanitizer.encodeForLog;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@RestController
public class WebhookController {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookController.class);

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
            @RequestBody String body) {
        try {
            if (service.isKnownWebhookType(type)) {
                try {
                    if (service.isValid(type, httpHeaders, body)) {
                        var headers = httpHeaders.toSingleValueMap();
                        var result = service.recordWebhook(type, body, headers);

                        return ResponseEntity.ok()
                                .body(
                                        WebhookResult.builder()
                                                .id(result.getId())
                                                .reference(result.getReference())
                                                .status(SC_OK)
                                                .message("Webhook Received")
                                                .build());
                    }
                    var logBody = encodeForLog(body);
                    LOG.error("Invalid Webhook: {}", logBody);
                    return ResponseEntity.badRequest()
                            .body(
                                    WebhookResult.builder()
                                            .status(SC_BAD_REQUEST)
                                            .message("Error processing webhook")
                                            .build());
                } catch (Exception ex) {
                    LOG.error("Error processing webhook", ex);
                    return ResponseEntity.internalServerError()
                            .body(
                                    WebhookResult.builder()
                                            .status(SC_INTERNAL_SERVER_ERROR)
                                            .message("Error processing webhook")
                                            .build());
                }
            }
            LOG.error("Invalid webhook type {}", type);
            return ResponseEntity.status(SC_NOT_FOUND)
                    .body(
                            WebhookResult.builder()
                                    .status(SC_NOT_FOUND)
                                    .message("Not found")
                                    .build());

        } catch (NullPointerException ex) {
            LOG.error("Null webhook type encountered", ex);
            return ResponseEntity.status(SC_NOT_FOUND)
                    .body(
                            WebhookResult.builder()
                                    .status(SC_NOT_FOUND)
                                    .message("Not found")
                                    .build());
        }
    }
}
