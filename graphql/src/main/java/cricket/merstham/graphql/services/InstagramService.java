package cricket.merstham.graphql.services;

import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.APINodeList;
import com.facebook.ads.sdk.Page;
import com.facebook.ads.sdk.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

import static java.text.MessageFormat.format;

@Service
public class InstagramService {
    private static Logger LOG = LoggerFactory.getLogger(InstagramService.class);
    private static final ZoneId UTC = ZoneId.of("UTC");

    private final APIContext apiContext;
    private final String facebookPageId;
    private final boolean debug;

    @Autowired
    public InstagramService(
            @Value("${configuration.facebook.application-secret}") String facebookAppSecret,
            @Value("${configuration.facebook.page-id}") String facebookPageId,
            @Value("${configuration.facebook.access-token}") String accessToken,
            @Value("${debug}") boolean debug) {
        this.apiContext = new APIContext(accessToken, facebookAppSecret);
        this.facebookPageId = facebookPageId;
        this.debug = debug;
    }

    public String createPost(String message, String image, String link) throws APIException {
        var page =
                new Page(facebookPageId, getPageTokenContext())
                        .get()
                        .requestConnectedInstagramAccountField()
                        .execute();

        var account = page.getFieldConnectedInstagramAccount();
        var post =
                account.createMedia()
                        .setImageUrl(image)
                        .setCaption(format("{0}\n\n{1}", message, link))
                        .execute();

        var publish = account.createMediaPublish().setCreationId(post.getId()).execute();
        LOG.info("Created instagram post {}", post.getId());
        return publish.getId();
    }

    public void deletePost(String id) {
        LOG.info("Deleting Instagram via API not supported - Request {}", id);
    }

    private APIContext getPageTokenContext() throws APIException {
        APINodeList<Page> pages =
                new User("me", apiContext).getAccounts().requestAccessTokenField().execute();
        var pageToken =
                pages.stream()
                        .filter(p -> p.getId().equals(facebookPageId))
                        .findFirst()
                        .map(Page::getFieldAccessToken)
                        .orElseThrow();
        return new APIContext(pageToken).enableDebug(debug);
    }
}
