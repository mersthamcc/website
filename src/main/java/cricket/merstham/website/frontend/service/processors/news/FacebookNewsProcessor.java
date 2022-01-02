package cricket.merstham.website.frontend.service.processors.news;

import com.facebook.ads.sdk.APIException;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.model.News;
import cricket.merstham.website.frontend.service.FacebookPageService;
import cricket.merstham.website.frontend.service.processors.ItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service("NewsFacebook")
public class FacebookNewsProcessor implements ItemProcessor<News> {
    public static final String FACEBOOK_ID = "facebook_id";
    private static Logger LOG = LoggerFactory.getLogger(FacebookNewsProcessor.class);

    private final String baseUrl;
    private final FacebookPageService facebookPageService;

    @Autowired
    public FacebookNewsProcessor(@Value("${base-url}") String baseUrl, FacebookPageService facebookPageService) {
        this.baseUrl = baseUrl;
        this.facebookPageService = facebookPageService;
    }

    @Override
    public void postOpen(News item) {
        item.setPublishToFacebook(hasFacebookPost(item));
    }

    @Override
    public void postSave(News item) {
        try {
            if (!item.isDraft()) {
                LOG.info("Running Facebook processor on news item '{}'", item.getTitle());
                if (item.isPublishToFacebook() && !hasFacebookPost(item)) {
                    var id = facebookPageService.createFacebookPost(
                            isBlank(item.getSocialSummary()) ? item.getTitle() : item.getSocialSummary(),
                            baseUrl + item.getLink().toString(),
                            item.getPublishDate());
                    item.getAttributes().put(FACEBOOK_ID, id);
                } else if (!item.isPublishToFacebook()
                        && hasFacebookPost(item)) {
                    facebookPageService.deletePost(item.getAttributes().get(FACEBOOK_ID));
                    item.getAttributes().put(FACEBOOK_ID, "");
                } else if (item.isPublishToFacebook() && hasFacebookPost(item)) {
                    facebookPageService.updateFacebookPost(
                            item.getAttributes().get(FACEBOOK_ID),
                            isBlank(item.getSocialSummary()) ? item.getTitle() : item.getSocialSummary(),
                            item.getPublishDate());
                }
            } else if (hasFacebookPost(item)) {
                facebookPageService.deletePost(item.getAttributes().get(FACEBOOK_ID));
                item.getAttributes().put(FACEBOOK_ID, "");
            }
        } catch (APIException ex) {
            throw new EntitySaveException("Error processing Facebook post",
                    List.of(ex.getMessage()));
        }
    }

    private boolean hasFacebookPost(News item) {
        return item.getAttributes().containsKey(FACEBOOK_ID) && isNotBlank(item.getAttributes().get(FACEBOOK_ID));
    }
}
