package cricket.merstham.website.frontend.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "mail")
@Data
public class MailConfiguration {
    private String region;
    private String fromAddress;
    private List<String> confirmationEmailBcc;
}
