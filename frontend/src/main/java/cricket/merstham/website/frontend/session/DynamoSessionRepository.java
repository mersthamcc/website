package cricket.merstham.website.frontend.session;

import cricket.merstham.shared.utils.IdGenerator;
import cricket.merstham.shared.utils.ObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.SessionRepository;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

public class DynamoSessionRepository implements SessionRepository<DynamoSession> {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoSessionRepository.class);
    private final DynamoDbClient client;
    private final DynamoSessionConfiguration configuration;
    private final ObjectSerializer<DynamoSession> serializer = new ObjectSerializer<>();

    public DynamoSessionRepository(
            DynamoDbClient client, DynamoSessionConfiguration configuration) {
        this.client = client;
        this.configuration = configuration;
    }

    @Override
    public DynamoSession createSession() {
        return new DynamoSession(
                IdGenerator.generate(), configuration.getMaxInactiveInterval().toSeconds());
    }

    @Override
    public void save(DynamoSession session) {
        client.putItem(
                PutItemRequest.builder()
                        .tableName(configuration.getTableName())
                        .item(toDynamoDBItem(session))
                        .build());
    }

    @Override
    public DynamoSession findById(String id) {
        var result =
                client.getItem(
                        GetItemRequest.builder()
                                .tableName(configuration.getTableName())
                                .key(
                                        Map.of(
                                                configuration.getSessionIdAttributeName(),
                                                AttributeValue.fromS(id)))
                                .build());
        if (result.hasItem()) {
            var session = toSession(result.item());
            if (nonNull(session) && !session.isExpired()) {
                session.setLastAccessedTime(Instant.now());
                return session;
            }

            LOG.info("Session: '{}' has expired. It will be deleted.", id);
            deleteById(id);
        }
        return null;
    }

    @Override
    public void deleteById(String id) {
        client.deleteItem(
                DeleteItemRequest.builder()
                        .tableName(configuration.getTableName())
                        .key(
                                Map.of(
                                        configuration.getSessionIdAttributeName(),
                                        AttributeValue.fromS(id)))
                        .build());
    }

    private Map<String, AttributeValue> toDynamoDBItem(DynamoSession session) {
        var map = new HashMap<String, AttributeValue>();
        map.put(configuration.getSessionIdAttributeName(), AttributeValue.fromS(session.getId()));
        map.put(
                configuration.getTtlAttributeName(),
                AttributeValue.fromN(Long.toString(calculateTimeToLive(session))));

        map.put(
                configuration.getSessionDataAttributeName(),
                AttributeValue.fromB(
                        serializer.serializeAndWrap(session, SdkBytes::fromByteArray)));
        return map;
    }

    private DynamoSession toSession(Map<String, AttributeValue> item) {
        return serializer.deserialize(
                item.get(configuration.getSessionDataAttributeName()).b().asByteArray());
    }

    private long calculateTimeToLive(DynamoSession session) {
        return session.getLastAccessedTime()
                .plusSeconds(session.getMaxInactiveInterval().getSeconds())
                .getEpochSecond();
    }
}
