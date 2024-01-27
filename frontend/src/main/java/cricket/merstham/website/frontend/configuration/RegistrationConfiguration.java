package cricket.merstham.website.frontend.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "registration")
@Data
public class RegistrationConfiguration {
    private List<CategoryDefaults> defaults;
}
