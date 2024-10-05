package cricket.merstham.graphql.services.processors.news;

import com.facebook.ads.sdk.APIException;
import cricket.merstham.graphql.entity.NewsEntity;
import cricket.merstham.graphql.services.InstagramService;
import cricket.merstham.graphql.services.processors.ItemProcessor;
import cricket.merstham.shared.dto.News;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static cricket.merstham.graphql.helpers.UriHelper.resolveUrl;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service("NewsInstagram")
public class InstagramNewsProcessor implements ItemProcessor<News, NewsEntity> {
    public static final String INSTAGRAM_ID = "instagram_id";
    private static final Logger LOG = LoggerFactory.getLogger(InstagramNewsProcessor.class);

    private final String baseUrl;
    private final InstagramService instagramService;

    @Autowired
    public InstagramNewsProcessor(
            @Value("${configuration.base-url}") String baseUrl, InstagramService instagramService) {
        this.baseUrl = baseUrl;
        this.instagramService = instagramService;
    }

    @Override
    public void postOpen(News response, NewsEntity entity) {
        response.setPublishToInstagram(hasInstagramPost(entity));
    }

    @Override
    public void postSave(News request, NewsEntity entity) {
        try {
            if (!request.isDraft()) {
                LOG.info("Running Instagram processor on news item '{}'", entity.getTitle());
                if (request.isPublishToInstagram() && !hasInstagramPost(entity)) {
                    var id =
                            instagramService.createPost(
                                    isBlank(entity.getSocialSummary())
                                            ? entity.getTitle()
                                            : entity.getSocialSummary(),
                                    request.getSocialImage(),
                                    resolveUrl(baseUrl, "news", entity.getPath()));
                    entity.getAttributes().put(INSTAGRAM_ID, id);
                } else if (!request.isPublishToInstagram() && hasInstagramPost(entity)) {
                    instagramService.deletePost(entity.getAttributes().get(INSTAGRAM_ID));
                    entity.getAttributes().remove(INSTAGRAM_ID);
                }
            } else if (hasInstagramPost(entity)) {
                instagramService.deletePost(entity.getAttributes().get(INSTAGRAM_ID));
                entity.getAttributes().remove(INSTAGRAM_ID);
            }
        } catch (APIException ex) {
            throw new RuntimeException(
                    "Error processing Instagram post: " + ex.getRawResponse(), ex);
        }
    }

    private boolean hasInstagramPost(NewsEntity entity) {
        return entity.getAttributes().containsKey(INSTAGRAM_ID)
                && isNotBlank(entity.getAttributes().get(INSTAGRAM_ID));
    }
}
