package cricket.merstham.graphql.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.cache.dynamodb")
public class DynamoCacheConfiguration {
    private String tableName;
    private String cacheNameAttributeName = "cacheName";
    private String keyAttributeName = "cacheKey";
    private String dataAttributeName = "data";
    private String timeToLiveAttributeName = "ttl";
    private int timeToLive;
    private String region;
    private boolean createTable = true;
    private URI endpoint = null;
}
