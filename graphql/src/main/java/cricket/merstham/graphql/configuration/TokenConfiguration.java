package cricket.merstham.graphql.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "configuration.token-service")
@Data
public class TokenConfiguration {
    private String vaultPath;
    private String credentialsPath;
    private Map<String, Token> tokens = new HashMap<>();

    @Data
    public static class Token {
        private String server;
        private boolean pkce;
        private String scopes;
    }
}
