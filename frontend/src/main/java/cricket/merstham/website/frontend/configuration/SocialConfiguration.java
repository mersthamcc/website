package cricket.merstham.website.frontend.configuration;

public class SocialConfiguration {
    private TwitterConfiguration twitter;
    private FacebookConfiguration facebook;

    public TwitterConfiguration getTwitter() {
        return twitter;
    }

    public SocialConfiguration setTwitter(TwitterConfiguration twitter) {
        this.twitter = twitter;
        return this;
    }

    public FacebookConfiguration getFacebook() {
        return facebook;
    }

    public SocialConfiguration setFacebook(FacebookConfiguration facebook) {
        this.facebook = facebook;
        return this;
    }

    public abstract static class BaseConfiguration {
        protected String handle;

        public String getHandle() {
            return handle;
        }

        public BaseConfiguration setHandle(String handle) {
            this.handle = handle;
            return this;
        }
    }

    public static class TwitterConfiguration extends BaseConfiguration {}

    public static class FacebookConfiguration extends BaseConfiguration {}
}
