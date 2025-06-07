package cricket.merstham.graphql.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "spring.security.api-key")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyConfig {
    private String headerName;
    private List<ApiKey> keys;
}
