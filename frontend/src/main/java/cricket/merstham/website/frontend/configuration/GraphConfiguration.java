package cricket.merstham.website.frontend.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
@ConfigurationProperties(prefix = "api")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphConfiguration {
    private URI graphUri;
}
