package cricket.merstham.website.frontend.session;

import cricket.merstham.shared.utils.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.session.SessionRepository;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

public class DynamoSessionRepository implements SessionRepository<DynamoSession> {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoSessionRepository.class);
    private final DynamoDbClient client;
    private final DynamoSessionConfiguration configuration;

    public DynamoSessionRepository(
            DynamoDbClient client, DynamoSessionConfiguration configuration) {
        this.client = client;
        this.configuration = configuration;
    }

    @Override
    public DynamoSession createSession() {
        return new DynamoSession(
                IdGenerator.generate(), configuration.getMaxInactiveIntervalInSeconds());
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
                AttributeValue.fromB(sessionToBinary(session)));
        return map;
    }

    private SdkBytes sessionToBinary(DynamoSession session) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream =
                        new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(session);

            objectOutputStream.flush();
            return SdkBytes.fromByteArray(byteArrayOutputStream.toByteArray());
        } catch (IOException ex) {
            LOG.error("Error serializing session", ex);
            return SdkBytes.fromByteArray(new byte[0]);
        }
    }

    private DynamoSession toSession(Map<String, AttributeValue> item) {
        try (ByteArrayInputStream byteArrayInputStream =
                        new ByteArrayInputStream(
                                item.get(configuration.getSessionDataAttributeName())
                                        .b()
                                        .asByteArray());
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (DynamoSession) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            LOG.error("Error deserialising session", ex);
            return null;
        }
    }

    private long calculateTimeToLive(DynamoSession session) {
        return session.getLastAccessedTime()
                .plusSeconds(session.getMaxInactiveInterval().getSeconds())
                .getEpochSecond();
    }
}
