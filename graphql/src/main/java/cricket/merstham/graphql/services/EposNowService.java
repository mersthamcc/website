package cricket.merstham.graphql.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cricket.merstham.graphql.entity.MemberEntity;
import cricket.merstham.graphql.repository.MemberEntityRepository;
import io.micrometer.core.annotation.Timed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static cricket.merstham.shared.IdentifierConstants.EPOS_CUSTOMER_ID;
import static java.util.Objects.nonNull;

@Service
public class EposNowService {
    private static final Logger LOG = LogManager.getLogger(EposNowService.class);

    private final MemberEntityRepository repository;
    private final String createFunctionArn;
    private final ObjectMapper objectMapper;
    private final LambdaClient client;

    private final boolean dryRun;

    @Autowired
    public EposNowService(
            MemberEntityRepository repository,
            @Value("${configuration.epos-now.create-function-arn}") String createFunctionArn,
            @Value("${configuration.epos-now.dry-run:true}") boolean dryRun,
            ObjectMapper objectMapper,
            LambdaClient client) {
        this.repository = repository;
        this.createFunctionArn = createFunctionArn;
        this.objectMapper = objectMapper;
        this.client = client;
        this.dryRun = dryRun;
    }

    @Scheduled(
            cron = "${configuration.epos-now.customer-refresh-cron}",
            zone = "${configuration.scheduler-zone}")
    @Timed(value = "eposnow.customer.sync", description = "Time taken to sync members with EposNow")
    @Transactional(propagation = Propagation.REQUIRED)
    public void eposNowSync() {
        LOG.info("Starting EPOS Sync...");
        var membersToProcess =
                repository.findAllWhereIdentifiersDoesNotContainKey(EPOS_CUSTOMER_ID);
        LOG.info("{} found to send.", membersToProcess.size());
        if (!membersToProcess.isEmpty()) {
            membersToProcess.forEach(this::sendToEposNow);
            repository.saveAllAndFlush(membersToProcess);
        }
        LOG.info("Finished EPOS Sync!");
    }

    private void sendToEposNow(MemberEntity member) {
        LOG.info("Sending member {} to EposNow...", member.getId());
        try {
            Map<String, Object> request = new HashMap<>();
            request.putAll(
                    Map.of(
                            "id", member.getId(),
                            "firstName", member.getStringAttribute("given-name"),
                            "lastName", member.getStringAttribute("family-name"),
                            "category",
                                    member.getMostRecentSubscription()
                                            .getPricelistItem()
                                            .getMemberCategory()
                                            .getKey(),
                            "registrationDate", member.getRegistrationDate(),
                            "reference",
                                    member.getMostRecentSubscription()
                                            .getOrder()
                                            .getUuid()
                                            .toString()));
            var email = determineEmail(member);
            if (nonNull(email)) {
                request.put("emailAddress", determineEmail(member));
            }
            if (nonNull(createFunctionArn) && !createFunctionArn.isBlank()) {
                var response =
                        client.invoke(
                                InvokeRequest.builder()
                                        .functionName(createFunctionArn)
                                        .payload(
                                                SdkBytes.fromByteArray(
                                                        objectMapper.writeValueAsBytes(request)))
                                        .invocationType(
                                                dryRun
                                                        ? InvocationType.DRY_RUN
                                                        : InvocationType.REQUEST_RESPONSE)
                                        .build());
                var result = objectMapper.readTree(response.payload().asByteArray());
                if (nonNull(response.functionError()) && !response.functionError().isBlank()) {
                    LOG.error("Function error: {}", response.functionError());
                } else {
                    var idNode = result.get("id");
                    if (nonNull(idNode)) {
                        member.getIdentifiers()
                                .put(EPOS_CUSTOMER_ID, Integer.toString(idNode.asInt()));
                    }
                }
            } else {
                var payload = objectMapper.convertValue(request, JsonNode.class);
                if (nonNull(payload)) {
                    LOG.info("EPOS Payload to send: {}", payload.toPrettyString());
                }
                member.getIdentifiers().put(EPOS_CUSTOMER_ID, "dummy");
            }
        } catch (Exception ex) {
            LOG.error("Error processing member", ex);
        }
    }

    private String determineEmail(MemberEntity member) {
        try {
            return member.getStringAttribute("email");
        } catch (NoSuchElementException ignored) {
        }

        try {
            return member.getStringAttribute("parent-email-1");
        } catch (NoSuchElementException ignored) {
            return null;
        }
    }
}
