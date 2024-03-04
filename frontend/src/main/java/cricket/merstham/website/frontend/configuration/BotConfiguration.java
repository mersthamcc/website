package cricket.merstham.website.frontend.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bots")
@Data
public class BotConfiguration {
    private String robots;
    private String sitemap;
}
