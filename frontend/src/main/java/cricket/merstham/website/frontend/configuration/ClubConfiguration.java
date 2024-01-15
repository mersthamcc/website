package cricket.merstham.website.frontend.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "club")
@Data
public class ClubConfiguration {
    private String clubName;
    private String logo;
    private String favicon;
    private String phoneNumber;
    private String clubAddress;
    private PlayCricketConfiguration playCricket;
    private SocialConfiguration social;
    private CookieConfiguration cookies;
    private String googleAnalyticsKey;
    private String googleMapsApiKey;
    private Fundraising fundraising;
}
