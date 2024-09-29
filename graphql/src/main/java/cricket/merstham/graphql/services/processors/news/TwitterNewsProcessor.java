package cricket.merstham.graphql.services.processors.news;

import cricket.merstham.graphql.entity.NewsEntity;
import cricket.merstham.graphql.services.TweetService;
import cricket.merstham.graphql.services.processors.ItemProcessor;
import cricket.merstham.shared.dto.News;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static cricket.merstham.graphql.helpers.UriHelper.resolveUrl;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Service("NewsTwitter")
public class TwitterNewsProcessor implements ItemProcessor<News, NewsEntity> {
    public static final String TWEET_ID = "tweet_id";
    private static final Logger LOG = LoggerFactory.getLogger(TwitterNewsProcessor.class);

    private final String baseUrl;
    private final TweetService tweetService;

    @Autowired
    public TwitterNewsProcessor(
            @Value("${configuration.base-url}") String baseUrl, TweetService tweetService) {
        this.baseUrl = baseUrl;
        this.tweetService = tweetService;
    }

    @Override
    public void postOpen(News request, NewsEntity entity) {
        request.setPublishToTwitter(hasTweet(entity));
    }

    @Override
    public void postSave(News request, NewsEntity entity) {
        try {
            if (!request.isDraft()) {
                LOG.info("Running Tweet processor on news item '{}'", entity.getTitle());
                if (request.isPublishToTwitter() && !hasTweet(entity)) {
                    var id =
                            tweetService.tweet(
                                    isBlank(entity.getSocialSummary())
                                            ? entity.getTitle()
                                            : entity.getSocialSummary(),
                                    resolveUrl(baseUrl, "news", entity.getPath()));
                    entity.getAttributes().put(TWEET_ID, id);
                } else if (!request.isPublishToTwitter() && hasTweet(entity)) {
                    tweetService.unTweet(entity.getAttributes().get(TWEET_ID));
                    entity.getAttributes().remove(TWEET_ID);
                }
            } else if (hasTweet(entity)) {
                tweetService.unTweet(entity.getAttributes().get(TWEET_ID));
                entity.getAttributes().remove(TWEET_ID);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error processing Tweet", ex);
        }
    }

    private boolean hasTweet(NewsEntity entity) {
        return entity.getAttributes().containsKey(TWEET_ID);
    }
}
