package cricket.merstham.website.frontend.configuration;

public class PlayCricketConfiguration {
    private boolean enabled;
    private String site;

    public boolean isEnabled() {
        return enabled;
    }

    public PlayCricketConfiguration setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getSite() {
        return site;
    }

    public PlayCricketConfiguration setSite(String site) {
        this.site = site;
        return this;
    }
}
