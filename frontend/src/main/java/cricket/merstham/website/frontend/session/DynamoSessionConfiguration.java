package cricket.merstham.website.frontend.session;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.session.dynamodb")
public class DynamoSessionConfiguration {
    private Integer maxInactiveIntervalInSeconds;
    private String tableName;
    private String sessionIdAttributeName = "sessionId";
    private String ttlAttributeName = "ttl";
    private String sessionDataAttributeName = "sessionData";
    private String region;
    private boolean createTable = true;
    private URI endpoint = null;
}
