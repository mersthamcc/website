package cricket.merstham.graphql.cache;

import cricket.merstham.shared.utils.ObjectSerializer;
import org.springframework.cache.Cache;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ComparisonOperator;
import software.amazon.awssdk.services.dynamodb.model.Condition;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Callable;

public class DynamoCache implements Cache {

    private final String name;
    private final DynamoDbClient client;
    private final DynamoCacheConfiguration configuration;
    private final ObjectSerializer<Object> serializer = new ObjectSerializer<>();

    public DynamoCache(String name, DynamoDbClient client, DynamoCacheConfiguration configuration) {
        this.name = name;
        this.client = client;
        this.configuration = configuration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return client;
    }

    @Override
    public ValueWrapper get(Object key) {
        var item = getItem(key);
        if (item.hasItem()) return () -> decodeItemData(item, Object.class);
        return null;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        var item = getItem(key);

        return decodeItemData(item, type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Callable<T> valueLoader) {
        ValueWrapper result = get(key);

        return result != null ? (T) result.get() : putCacheValue(key, valueLoader);
    }

    private <T> T putCacheValue(Object key, Callable<T> valueLoader) {
        try {
            var value = valueLoader.call();

            client.putItem(
                    PutItemRequest.builder()
                            .tableName(configuration.getTableName())
                            .item(toItem(key, value))
                            .build());

            return value;

        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }

    @Override
    public void put(Object key, Object value) {
        putCacheValue(key, () -> value);
    }

    @Override
    public void evict(Object key) {
        client.deleteItem(
                DeleteItemRequest.builder()
                        .tableName(configuration.getTableName())
                        .key(toKey(key))
                        .build());
    }

    @Override
    public void clear() {
        Map<String, AttributeValue> startKey = Map.of();
        QueryResponse result;
        do {
            result =
                    client.query(
                            QueryRequest.builder()
                                    .keyConditions(
                                            Map.of(
                                                    configuration.getCacheNameAttributeName(),
                                                    Condition.builder()
                                                            .comparisonOperator(
                                                                    ComparisonOperator.EQ)
                                                            .attributeValueList(
                                                                    AttributeValue.fromS(name))
                                                            .build()))
                                    .consistentRead(true)
                                    .exclusiveStartKey(startKey)
                                    .limit(300)
                                    .attributesToGet(configuration.getKeyAttributeName())
                                    .build());

            if (result.hasItems()) {
                for (Map<String, AttributeValue> item : result.items()) {
                    evict(item.get(configuration.getKeyAttributeName()));
                }
            }
            startKey = result.lastEvaluatedKey();
        } while (result.hasLastEvaluatedKey() && !result.lastEvaluatedKey().isEmpty());
    }

    private <T> T decodeItemData(GetItemResponse item, Class<T> type) {
        if (item.hasItem()) {
            var data =
                    serializer.deserialize(
                            item.item()
                                    .get(configuration.getDataAttributeName())
                                    .b()
                                    .asByteArray());
            try {
                return type.cast(data);
            } catch (ClassCastException ignored) {
            }
        }
        return null;
    }

    private Map<String, AttributeValue> toKey(Object key) {
        return Map.of(
                configuration.getCacheNameAttributeName(), AttributeValue.fromS(name),
                configuration.getKeyAttributeName(),
                        AttributeValue.fromB(
                                serializer.serializeAndWrap(key, SdkBytes::fromByteArray)));
    }

    private Map<String, AttributeValue> toItem(Object key, Object value) {
        return Map.of(
                configuration.getCacheNameAttributeName(), AttributeValue.fromS(name),
                configuration.getKeyAttributeName(),
                        AttributeValue.fromB(
                                serializer.serializeAndWrap(key, SdkBytes::fromByteArray)),
                configuration.getTimeToLiveAttributeName(), calculateTtl(),
                configuration.getDataAttributeName(),
                        AttributeValue.fromB(
                                serializer.serializeAndWrap(value, SdkBytes::fromByteArray)));
    }

    private AttributeValue calculateTtl() {
        var ttl = Instant.now().plusSeconds(configuration.getTimeToLive());
        return AttributeValue.fromN(Long.toString(ttl.getEpochSecond()));
    }

    private GetItemResponse getItem(Object key) {
        return client.getItem(
                GetItemRequest.builder()
                        .tableName(configuration.getTableName())
                        .key(toKey(key))
                        .build());
    }
}
