package cricket.merstham.website.frontend.service;

import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.APINodeList;
import com.facebook.ads.sdk.Page;
import com.facebook.ads.sdk.Post;
import com.facebook.ads.sdk.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FacebookPageService {
    private static Logger LOG = LoggerFactory.getLogger(FacebookPageService.class);

    private final APIContext apiContext;
    private final String facebookPageId;

    @Autowired
    public FacebookPageService(
            @Value("${facebook.application-secret}") String facebookAppSecret,
            @Value("${facebook.page-id}") String facebookPageId,
            @Value("${facebook.access-token}") String accessToken) {
        this.apiContext = new APIContext(accessToken, facebookAppSecret);
        this.facebookPageId = facebookPageId;
    }

    public String createFacebookPost(String message, String link) throws APIException {
        var context = getPageTokenContext();
        var page = new Page(facebookPageId, context).get().execute();

        var post =
                page.createFeed()
                        .setMessage(message)
                        .setLink(link)
                        .execute();

        LOG.info("Created facebook post {}", post.getId());
        return post.getId();
    }

    public void deletePost(String id) throws APIException {
        var context = getPageTokenContext();
        var post = new Post(id, context).delete().execute();

        LOG.info("Deleted facebook post {}", id);
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
