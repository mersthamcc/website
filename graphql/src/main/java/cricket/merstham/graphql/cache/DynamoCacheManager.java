package cricket.merstham.graphql.cache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DynamoCacheManager implements CacheManager {

    private static final Logger LOG = LogManager.getLogger(DynamoCacheManager.class);

    private final DynamoDbClient client;
    private final DynamoCacheConfiguration configuration;
    private final Map<String, DynamoCache> caches = new HashMap<>();

    public DynamoCacheManager(DynamoDbClient client, DynamoCacheConfiguration configuration) {
        this.client = client;
        this.configuration = configuration;
    }

    @Override
    public Cache getCache(String name) {
        if (caches.containsKey(name)) {
            LOG.info("Reusing existing cache {}", name);
            return caches.get(name);
        }

        LOG.info("Creating new cache {}", name);
        var cache = new DynamoCache(name, client, configuration);
        caches.put(name, cache);
        return cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return caches.keySet();
    }
}
