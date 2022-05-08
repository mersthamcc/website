package cricket.merstham.website.frontend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import static java.lang.String.format;

@Service
public class TweetService {
    private static final String API_URL = "https://api.twitter.com/2/tweets";

    private final TwitterFactory twitterFactory;

    @Autowired
    public TweetService(
            @Value("${twitter.api-key}") String apiKey,
            @Value("${twitter.api-secret}") String apiSecret,
            @Value("${twitter.oauth-access-token}") String accessToken,
            @Value("${twitter.oauth-access-token-secret}") String accessTokenSecret) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(apiKey)
                .setOAuthConsumerSecret(apiSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);
        this.twitterFactory = new TwitterFactory(cb.build());
    }

    public long tweet(String message, String link) throws TwitterException {
        Twitter twitter = twitterFactory.getInstance();

        Status status = twitter.updateStatus(format("%s %s", message, link));
        return status.getId();
    }

    public void unTweet(long id) throws TwitterException {
        Twitter twitter = twitterFactory.getInstance();

        twitter.destroyStatus(id);
    }
}
