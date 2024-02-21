package cricket.merstham.graphql.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cricket.merstham.graphql.entity.WebhookReceivedEntity;
import cricket.merstham.graphql.repository.PaymentEntityRepository;
import cricket.merstham.graphql.repository.WebhookReceivedRepository;
import cricket.merstham.graphql.services.webhooks.WebhookProcessor;
import cricket.merstham.graphql.services.webhooks.WebhookProcessorManager;
import cricket.merstham.shared.dto.WebhookReceived;
import io.micrometer.core.annotation.Timed;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class WebhookService {

    private static final Logger LOG = LoggerFactory.getLogger(WebhookService.class);

    private final WebhookReceivedRepository repository;
    private final PaymentEntityRepository paymentRepository;
    private final ModelMapper modelMapper;
    private final WebhookProcessorManager manager;
    private final ObjectMapper objectMapper;

    @Autowired
    public WebhookService(
            WebhookReceivedRepository repository,
            PaymentEntityRepository paymentRepository,
            ModelMapper modelMapper,
            WebhookProcessorManager manager,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.paymentRepository = paymentRepository;
        this.modelMapper = modelMapper;
        this.manager = manager;
        this.objectMapper = objectMapper;
    }

    public WebhookReceived recordWebhook(String type, String body, Map<String, String> headers)
            throws JsonProcessingException {
        var serialisedHeaders = objectMapper.valueToTree(headers);
        var json = objectMapper.readTree(body);
        var processor = getProcessor(type);
        var entity =
                repository.save(
                        WebhookReceivedEntity.builder()
                                .receivedDate(Instant.now())
                                .type(type)
                                .reference(processor.getId(json))
                                .processed(false)
                                .headers(serialisedHeaders)
                                .body(json)
                                .build());

        return modelMapper.map(entity, WebhookReceived.class);
    }

    public boolean isKnownWebhookType(String type) {
        return manager.exists(type);
    }

    public boolean isValid(String type, HttpHeaders httpHeaders, String body) {
        return getProcessor(type).isValid(httpHeaders, body);
    }

    @Scheduled(
            cron = "${configuration.accounting.sync-cron}",
            zone = "${configuration.scheduler-zone}")
    @Timed(
            value = "webhooks.processor.time_taken",
            description = "Time taken to process received payment webhooks")
    @Transactional(propagation = Propagation.REQUIRED)
    public void processWebhooks() {
        LOG.info("Processing received webhooks...");
        var webhooks = repository.findAllByProcessedFalseAndTypeIn(List.of("stripe"));

        webhooks.forEach(
                webhook -> {
                    var processor = getProcessor(webhook.getType());
                    var reference = processor.getPaymentReference(webhook.getBody());
                    var payment =
                            paymentRepository.findByTypeAndReference(webhook.getType(), reference);

                    if (payment.isPresent()) {
                        if (processor.processWebhook(webhook.getBody(), payment.get())) {
                            paymentRepository.saveAndFlush(payment.get());
                            LOG.info(
                                    "Payment updated - Type: {}, Reference: {}",
                                    webhook.getType(),
                                    reference);
                        } else {
                            LOG.warn(
                                    "Failed to process webhook - Type: {}, Reference: {}",
                                    webhook.getType(),
                                    reference);
                        }
                    } else {
                        LOG.warn(
                                "Payment not found - Type: {}, Reference: {}",
                                webhook.getType(),
                                reference);
                    }
                    webhook.setProcessed(true);
                });
        LOG.info("Finished processing received webhooks!");
    }

    private WebhookProcessor getProcessor(String type) {
        return manager.getProcessor(type);
    }
}
