package cricket.merstham.graphql.cache;

import cricket.merstham.shared.utils.ObjectSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.Cache;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ComparisonOperator;
import software.amazon.awssdk.services.dynamodb.model.Condition;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Callable;

import static java.text.MessageFormat.format;

public class DynamoCache implements Cache {

    private static final Logger LOG = LogManager.getLogger(DynamoCache.class);
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
        if (item.hasItem()) {
            LOG.info("Cache hit {} key {}", name, key);
            return () -> decodeItemData(item, Object.class);
        }
        LOG.info("Cache miss {} key {}", name, key);
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

    @Override
    public void put(Object key, Object value) {
        LOG.info("Putting cache value into {} key {}", name, key);
        putCacheValue(key, () -> value);
    }

    @Override
    public void evict(Object key) {
        LOG.info("Evicting cache value from {} key {}", name, key);
        var itemKey = toKey(key);
        try {
            client.deleteItem(
                    DeleteItemRequest.builder()
                            .tableName(configuration.getTableName())
                            .key(toKey(itemKey))
                            .build());
        } catch (DynamoDbException ex) {
            LOG.warn("Error evicting cache!", ex);
        }
    }

    @Override
    public void clear() {
        LOG.info("Clearing cache {}", name);
        Map<String, AttributeValue> startKey = null;
        QueryResponse result;
        do {
            result =
                    client.query(
                            QueryRequest.builder()
                                    .tableName(configuration.getTableName())
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
                    var key =
                            Map.of(
                                    configuration.getCacheNameAttributeName(),
                                            AttributeValue.fromS(name),
                                    configuration.getKeyAttributeName(),
                                            item.get(configuration.getKeyAttributeName()));
                    client.deleteItem(
                            DeleteItemRequest.builder()
                                    .tableName(configuration.getTableName())
                                    .key(key)
                                    .build());
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
            } catch (ClassCastException e) {
                LOG.error("Error decoding cache value", e);
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
        var ttl = Instant.now().plusSeconds(configuration.getTimeToLive().toSeconds());
        return AttributeValue.fromN(Long.toString(ttl.getEpochSecond()));
    }

    private GetItemResponse getItem(Object key) {
        LOG.info("Getting cache value {} key {}", name, key);
        return client.getItem(
                GetItemRequest.builder()
                        .tableName(configuration.getTableName())
                        .key(toKey(key))
                        .build());
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
            LOG.error(format("Error putting cache value {0} key {1}", name, key), e);
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }
}
