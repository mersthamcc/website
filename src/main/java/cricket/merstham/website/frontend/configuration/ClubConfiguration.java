package cricket.merstham.website.frontend.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "club")
public class ClubConfiguration {
    private String clubName;
    private String logo;
    private String phoneNumber;
    private PlayCricketConfiguration playCricket;
    private SocialConfiguration social;
    private CookieConfiguration cookies;
    private String googleAnalyticsKey;

    public String getClubName() {
        return clubName;
    }

    public ClubConfiguration setClubName(String clubName) {
        this.clubName = clubName;
        return this;
    }

    public String getLogo() {
        return logo;
    }

    public ClubConfiguration setLogo(String logo) {
        this.logo = logo;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public ClubConfiguration setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public PlayCricketConfiguration getPlayCricket() {
        return playCricket;
    }

    public ClubConfiguration setPlayCricket(PlayCricketConfiguration playCricket) {
        this.playCricket = playCricket;
        return this;
    }

    public SocialConfiguration getSocial() {
        return social;
    }

    public ClubConfiguration setSocial(SocialConfiguration social) {
        this.social = social;
        return this;
    }

    public CookieConfiguration getCookies() {
        return cookies;
    }

    public ClubConfiguration setCookies(CookieConfiguration cookies) {
        this.cookies = cookies;
        return this;
    }

    public String getGoogleAnalyticsKey() {
        return googleAnalyticsKey;
    }

    public ClubConfiguration setGoogleAnalyticsKey(String googleAnalyticsKey) {
        this.googleAnalyticsKey = googleAnalyticsKey;
        return this;
    }
}
