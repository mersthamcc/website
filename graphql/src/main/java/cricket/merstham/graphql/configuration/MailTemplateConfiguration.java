package cricket.merstham.graphql.configuration;

import io.rocketbase.mail.EmailTemplateBuilder;
import io.rocketbase.mail.config.TbConfiguration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.time.LocalDate.now;

@Configuration
@ConfigurationProperties(prefix = "configuration.mail")
@Data
public class MailTemplateConfiguration {

    private final TbConfiguration configuration;
    private String logo;
    private String logoDark;
    private String clubName;
    private String clubUrl;
    private String footerText;
    private String fromAddress;
    private List<String> membershipBcc;
    private String inclusiveKitUrl;
    private String inclusiveKitPartner;

    public MailTemplateConfiguration() {
        this.configuration = TbConfiguration.newInstance();
        this.configuration.getContent().setWidth(800);
        this.configuration.setDarkModeEnabled(true);
    }

    public EmailTemplateBuilder.EmailTemplateConfigBuilder getEmailBuilder() {
        return EmailTemplateBuilder.builder()
                .configuration(configuration)
                .header()
                .logo(logo)
                .logoDark(logoDark)
                .logoHeight("200px")
                .and()
                .copyright(clubName)
                .url(clubUrl)
                .year(now().getYear())
                .and()
                .footerText(footerText)
                .and();
    }
}
