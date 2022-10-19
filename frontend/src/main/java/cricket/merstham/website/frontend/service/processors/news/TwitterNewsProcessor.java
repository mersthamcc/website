package cricket.merstham.website.frontend.service.processors.news;

import cricket.merstham.shared.dto.News;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.service.TweetService;
import cricket.merstham.website.frontend.service.processors.ItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import twitter4j.TwitterException;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service("NewsTwitter")
public class TwitterNewsProcessor implements ItemProcessor<News> {
    public static final String TWEET_ID = "tweet_id";
    private static Logger LOG = LoggerFactory.getLogger(TwitterNewsProcessor.class);

    private final String baseUrl;
    private final TweetService tweetService;

    @Autowired
    public TwitterNewsProcessor(@Value("${base-url}") String baseUrl, TweetService tweetService) {
        this.baseUrl = baseUrl;
        this.tweetService = tweetService;
    }

    @Override
    public void postOpen(News item) {
        item.setPublishToTwitter(hasTweet(item));
    }

    @Override
    public List<String> preSave(News item) {
        if (item.isPublishToTwitter() && !item.isDraft()) {
            var scheduledPublishTime =
                    item.getPublishDate()
                            .atZone(ZoneId.systemDefault())
                            .withZoneSameInstant(ZoneId.of("UTC"))
                            .toInstant();
            if (scheduledPublishTime.isAfter(Instant.now())) {
                return List.of("Twitter does not allow scheduling of posts");
            }
        }
        return List.of();
    }

    @Override
    public void postSave(News item) {
        try {
            if (!item.isDraft()) {
                LOG.info("Running Tweet processor on news item '{}'", item.getTitle());
                if (item.isPublishToTwitter() && !hasTweet(item)) {
                    var id =
                            tweetService.tweet(
                                    isBlank(item.getSocialSummary())
                                            ? item.getTitle()
                                            : item.getSocialSummary(),
                                    baseUrl + item.getPath().toString());
                    item.setAttribute(TWEET_ID, Long.toString(id));
                } else if (!item.isPublishToTwitter() && hasTweet(item)) {
                    tweetService.unTweet(Long.parseLong(item.getAttribute(TWEET_ID)));
                    item.setAttribute(TWEET_ID, null);
                }
            } else if (hasTweet(item)) {
                tweetService.unTweet(Long.parseLong(item.getAttribute(TWEET_ID)));
                item.setAttribute(TWEET_ID, null);
            }
        } catch (TwitterException ex) {
            throw new EntitySaveException("Error processing Tweet", List.of(ex.getMessage()));
        }
    }

    private boolean hasTweet(News item) {
        return isNotBlank(item.getAttribute(TWEET_ID));
    }
}
