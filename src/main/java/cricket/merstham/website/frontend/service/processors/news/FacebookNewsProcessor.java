package cricket.merstham.website.frontend.service.processors.news;

import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.APINodeList;
import com.facebook.ads.sdk.Page;
import com.facebook.ads.sdk.User;
import cricket.merstham.website.frontend.model.News;
import cricket.merstham.website.frontend.service.processors.ItemProcessor;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("NewsFacebook")
public class FacebookNewsProcessor implements ItemProcessor<News> {
    public static final String FACEBOOK_ID = "facebook_id";
    private static Logger LOG = LoggerFactory.getLogger(FacebookNewsProcessor.class);

    private final APIContext apiContext;
    private final String facebookPageId;
    private final String baseUrl;

    @Autowired
    public FacebookNewsProcessor(
            @Value("${facebook.application-secret}") String facebookAppSecret,
            @Value("${facebook.page-id}") String facebookPageId,
            @Value("${facebook.access-token}") String accessToken,
            @Value("${facebook.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.apiContext = new APIContext(accessToken, facebookAppSecret);
        this.facebookPageId = facebookPageId;
    }

    @Override
    public void postProcessing(News item) {
        if (!item.isDraft()) {
            LOG.info("Running Facebook processor on news item '{}'", item.getTitle());
            if (item.isPublishToFacebook() && !item.getAttributes().containsKey(FACEBOOK_ID)) {
                try {
                    var context = getPageTokenContext();
                    var page = new Page(facebookPageId, context).get().execute();

                    var post =
                            page.createFeed()
                                    .setMessage(
                                            Strings.isBlank(item.getSocialSummary())
                                                    ? item.getTitle()
                                                    : item.getSocialSummary())
                                    .setLink(baseUrl + item.getLink().toString())
                                    .execute();

                    LOG.info("Created facebook post {}", post.getId());
                    item.getAttributes().put(FACEBOOK_ID, post.getId());
                } catch (APIException e) {
                    e.printStackTrace();
                }
            } else if (!item.isPublishToFacebook()
                    && item.getAttributes().containsKey(FACEBOOK_ID)) {
                // Delete FB post
            }
        } else if (item.getAttributes().containsKey(FACEBOOK_ID)) {
            // Delete FB post
        }
    }

    private APIContext getPageTokenContext() throws APIException {
        APINodeList<Page> pages =
                new User("me", apiContext).getAccounts().requestAccessTokenField().execute();
        var pageToken =
                pages.stream()
                        .filter(p -> p.getId().equals(facebookPageId))
                        .findFirst()
                        .map(p -> p.getFieldAccessToken())
                        .orElseThrow();
        return new APIContext(pageToken).enableDebug(false);
    }
}
