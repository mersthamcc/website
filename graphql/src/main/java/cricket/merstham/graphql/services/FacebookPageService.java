package cricket.merstham.graphql.services;

import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.APINodeList;
import com.facebook.ads.sdk.Page;
import com.facebook.ads.sdk.Post;
import com.facebook.ads.sdk.User;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class FacebookPageService {
    private static Logger LOG = LoggerFactory.getLogger(FacebookPageService.class);
    private static final ZoneId UTC = ZoneId.of("UTC");

    private final APIContext apiContext;
    private final String facebookPageId;
    private final boolean debug;

    @Autowired
    public FacebookPageService(
            @Value("${configuration.facebook.application-secret}") String facebookAppSecret,
            @Value("${configuration.facebook.page-id}") String facebookPageId,
            @Value("${configuration.facebook.access-token}") String accessToken,
            @Value("${debug}") boolean debug) {
        this.apiContext = new APIContext(accessToken, facebookAppSecret);
        this.facebookPageId = facebookPageId;
        this.debug = debug;
    }

    public String createFacebookPost(
            String message, String imageUrl, String link, LocalDateTime publishTime)
            throws APIException {
        var page = new Page(facebookPageId, getPageTokenContext()).get().execute();

        var scheduledPublishTime =
                publishTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(UTC).toInstant();

        var postRequest = page.createFeed().setMessage(message);

        if (Strings.isNotBlank(imageUrl)) {
            postRequest.setPicture(imageUrl);
        }

        if (!link.startsWith("http://localhost")) {
            postRequest.setLink(link);
        }

        if (scheduledPublishTime.isAfter(Instant.now())) {
            postRequest
                    .setScheduledPublishTime(Long.toString(scheduledPublishTime.getEpochSecond()))
                    .setPublished(false);
        }
        var post = postRequest.execute();
        LOG.info("Created facebook post {}", post.getId());
        return post.getId();
    }

    public void updateFacebookPost(String id, String message, LocalDateTime publishTime)
            throws APIException {
        var post = new Post(id, getPageTokenContext()).get().execute();

        var scheduledPublishTime =
                publishTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(UTC).toInstant();
        var postUpdateRequest = post.update().setMessage(message);

        if (scheduledPublishTime.isAfter(Instant.now())) {
            postUpdateRequest
                    .setScheduledPublishTime(Long.toString(scheduledPublishTime.getEpochSecond()))
                    .setIsPublished(false);
        }
        postUpdateRequest.execute();
        LOG.info("Updated facebook post {}", id);
    }

    public void deletePost(String id) throws APIException {
        new Post(id, getPageTokenContext()).delete().execute();

        LOG.info("Deleted facebook post {}", id);
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
