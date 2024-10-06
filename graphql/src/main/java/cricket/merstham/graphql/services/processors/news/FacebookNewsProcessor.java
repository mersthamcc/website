package cricket.merstham.graphql.services.processors.news;

import com.facebook.ads.sdk.APIException;
import cricket.merstham.graphql.entity.NewsEntity;
import cricket.merstham.graphql.services.FacebookPageService;
import cricket.merstham.graphql.services.processors.ItemProcessor;
import cricket.merstham.shared.dto.News;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static cricket.merstham.graphql.helpers.UriHelper.resolveUrl;
import static java.time.ZoneOffset.UTC;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service("NewsFacebook")
public class FacebookNewsProcessor implements ItemProcessor<News, NewsEntity> {
    public static final String FACEBOOK_ID = "facebook_id";
    private static final Logger LOG = LoggerFactory.getLogger(FacebookNewsProcessor.class);

    private final String baseUrl;
    private final FacebookPageService facebookPageService;

    @Autowired
    public FacebookNewsProcessor(
            @Value("${configuration.base-url}") String baseUrl,
            FacebookPageService facebookPageService) {
        this.baseUrl = baseUrl;
        this.facebookPageService = facebookPageService;
    }

    @Override
    public void postOpen(News response, NewsEntity entity) {
        response.setPublishToFacebook(hasFacebookPost(entity));
    }

    @Override
    public void postSave(News request, NewsEntity entity) {
        try {
            if (!request.isDraft()) {
                LOG.info("Running Facebook processor on news item '{}'", entity.getTitle());
                if (request.isPublishToFacebook() && !hasFacebookPost(entity)) {
                    var id =
                            facebookPageService.createFacebookPost(
                                    isBlank(entity.getSocialSummary())
                                            ? entity.getTitle()
                                            : entity.getSocialSummary(),
                                    request.getSocialImage(),
                                    resolveUrl(baseUrl, "news", entity.getPath()),
                                    entity.getPublishDate().atZone(UTC).toLocalDateTime());
                    entity.getAttributes().put(FACEBOOK_ID, id);
                } else if (!request.isPublishToFacebook() && hasFacebookPost(entity)) {
                    facebookPageService.deletePost(entity.getAttributes().get(FACEBOOK_ID));
                    entity.getAttributes().remove(FACEBOOK_ID);
                } else if (request.isPublishToFacebook() && hasFacebookPost(entity)) {
                    facebookPageService.updateFacebookPost(
                            entity.getAttributes().get(FACEBOOK_ID),
                            isBlank(entity.getSocialSummary())
                                    ? entity.getTitle()
                                    : entity.getSocialSummary(),
                            entity.getPublishDate().atZone(UTC).toLocalDateTime());
                }
            } else if (hasFacebookPost(entity)) {
                facebookPageService.deletePost(entity.getAttributes().get(FACEBOOK_ID));
                entity.getAttributes().remove(FACEBOOK_ID);
            }
        } catch (APIException ex) {
            throw new RuntimeException(
                    "Error processing Facebook post: " + ex.getRawResponse(), ex);
        }
    }

    private boolean hasFacebookPost(NewsEntity entity) {
        return entity.getAttributes().containsKey(FACEBOOK_ID)
                && isNotBlank(entity.getAttributes().get(FACEBOOK_ID));
    }
}
