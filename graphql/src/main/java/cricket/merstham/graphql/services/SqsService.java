package cricket.merstham.graphql.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import cricket.merstham.graphql.dto.spond.MatchFeePayment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SqsService {
    private static final Logger LOG = LoggerFactory.getLogger(SqsService.class);

    public static final String MESSAGE_TYPE_ATTRIBUTE = "message-type";
    public static final String MESSAGE_ID_ATTRIBUTE = "message-id";
    public static final String CUSTOMER_SYNC_TRANSACTION = "customer-sync";
    public static final String MATCH_FEE_TRANSACTION = "match-fee";
    public static final String MEMBER_ORDER_TRANSACTION = "member-order";
    public static final String MEMBER_PAYMENT_TRANSACTION = "member-payment";

    private final SqsClient client;
    private final String queueUrl;
    private final ObjectMapper objectMapper;

    @Autowired
    public SqsService(
            @Value("${configuration.transaction-queue-url}") String queueUrl,
            ObjectMapper objectMapper) {
        this.queueUrl = queueUrl;
        this.objectMapper = objectMapper;
        this.client =
                SqsClient.builder()
                        .region(Region.EU_WEST_2)
                        .credentialsProvider(DefaultCredentialsProvider.create())
                        .build();
    }

    public void sendCustomer(Object data) {
        sendToQueue(CUSTOMER_SYNC_TRANSACTION, data);
    }

    public void sendMatchFees(List<MatchFeePayment> payments) {
        payments.forEach(
                payment -> {
                    var id = sendToQueue(MATCH_FEE_TRANSACTION, payment);
                    LOG.info("Spond payment {} sent queue, message ID = {}", payment.getId(), id);
                });
    }

    private String sendToQueue(String transactionType, Object data) {
        var messageId = UUID.randomUUID().toString();
        MessageAttributeValue messageTypeAttribute =
                MessageAttributeValue.builder()
                        .dataType("String")
                        .stringValue(transactionType)
                        .build();
        MessageAttributeValue messageIdAttribute =
                MessageAttributeValue.builder().dataType("String").stringValue(messageId).build();
        try {
            SendMessageRequest request =
                    SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageAttributes(
                                    Map.of(
                                            MESSAGE_TYPE_ATTRIBUTE, messageTypeAttribute,
                                            MESSAGE_ID_ATTRIBUTE, messageIdAttribute))
                            .messageDeduplicationId(messageId)
                            .messageGroupId(transactionType)
                            .messageBody(objectMapper.writeValueAsString(data))
                            .build();
            var response = client.sendMessage(request);
            return response.messageId();
        } catch (Exception ex) {
            LOG.error("Error sending to queue", ex);
            throw new RuntimeException("Error sending to queue", ex);
        }
    }
}
