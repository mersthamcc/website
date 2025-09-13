package cricket.merstham.graphql.services;

import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static cricket.merstham.graphql.services.SqsService.CUSTOMER_SYNC_TRANSACTION;
import static cricket.merstham.graphql.services.SqsService.MESSAGE_ID_ATTRIBUTE;
import static cricket.merstham.graphql.services.SqsService.MESSAGE_TYPE_ATTRIBUTE;
import static cricket.merstham.shared.IdentifierConstants.EPOS_CUSTOMER_ID;

@Service
@Lazy
public class SqsListenerService {
    private static final Logger LOG = LoggerFactory.getLogger(SqsListenerService.class);

    private final MembershipService membershipService;

    @Autowired
    public SqsListenerService(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @SqsListener(
            queueNames = {"${configuration.transaction-response-queue-url}"},
            acknowledgementMode = "MANUAL",
            maxConcurrentMessages = "1",
            maxMessagesPerPoll = "1",
            messageVisibilitySeconds = "60")
    public CompletableFuture<Void> processQueue(
            Map<String, Object> message,
            @Header(MESSAGE_ID_ATTRIBUTE) String messageId,
            @Header(MESSAGE_TYPE_ATTRIBUTE) String messageType,
            Acknowledgement acknowledgement) {
        try {
            LOG.info(
                    "Received transaction response message {}, type {}: {}",
                    messageId,
                    messageType,
                    message);
            switch (messageType) {
                case CUSTOMER_SYNC_TRANSACTION -> {
                    if (message.containsKey("id") && message.containsKey("eposCustomerId")) {
                        var id = (int) message.get("id");
                        var eposCustomerId = message.get("eposCustomerId").toString();
                        membershipService.addMemberIdentifier(id, EPOS_CUSTOMER_ID, eposCustomerId);
                        LOG.info("Successfully added EPOS customer identifier to member {}", id);
                    } else {
                        LOG.error("EPOS customer sync message missing an identifier");
                    }
                }
                default -> throw new RuntimeException("Invalid message type");
            }
            acknowledgement.acknowledge();
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            LOG.error("Error processing queue message", e);
            return CompletableFuture.failedFuture(e);
        }
    }
}
