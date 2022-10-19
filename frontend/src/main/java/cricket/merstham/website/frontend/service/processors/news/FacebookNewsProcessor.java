package cricket.merstham.website.frontend.service.processors.news;

import com.facebook.ads.sdk.APIException;
import cricket.merstham.shared.dto.News;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.service.FacebookPageService;
import cricket.merstham.website.frontend.service.processors.ItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.apache.logging.log4j.util.Strings.isBlank;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Service("NewsFacebook")
public class FacebookNewsProcessor implements ItemProcessor<News> {
    public static final String FACEBOOK_ID = "facebook_id";
    private static Logger LOG = LoggerFactory.getLogger(FacebookNewsProcessor.class);

    private final String baseUrl;
    private final FacebookPageService facebookPageService;

    @Autowired
    public FacebookNewsProcessor(
            @Value("${base-url}") String baseUrl, FacebookPageService facebookPageService) {
        this.baseUrl = baseUrl;
        this.facebookPageService = facebookPageService;
    }

    @Override
    public void postOpen(News item) {
        item.setPublishToFacebook(hasFacebookPost(item));
    }

    @Override
    public List<String> preSave(News item) {
        if (item.isPublishToFacebook() && !item.isDraft()) {
            var scheduledPublishTime =
                    item.getPublishDate()
                            .atZone(ZoneId.systemDefault())
                            .withZoneSameInstant(ZoneId.of("UTC"))
                            .toInstant();
            if (scheduledPublishTime.isAfter(Instant.now())
                    && scheduledPublishTime.isBefore(Instant.now().plus(15, ChronoUnit.MINUTES))) {
                return List.of(
                        "When scheduling an item published to Facebook, publish time must be, at least, 15 minutes in the future");
            }
        }
        return List.of();
    }

    @Override
    public void postSave(News item) {
        try {
            if (!item.isDraft()) {
                LOG.info("Running Facebook processor on news item '{}'", item.getTitle());
                if (item.isPublishToFacebook() && !hasFacebookPost(item)) {
                    var id =
                            facebookPageService.createFacebookPost(
                                    isBlank(item.getSocialSummary())
                                            ? item.getTitle()
                                            : item.getSocialSummary(),
                                    baseUrl + item.getPath().toString(),
                                    item.getPublishDate().atZone(UTC).toLocalDateTime());
                    item.setAttribute(FACEBOOK_ID, id);
                } else if (!item.isPublishToFacebook() && hasFacebookPost(item)) {
                    facebookPageService.deletePost(item.getAttribute(FACEBOOK_ID));
                    item.setAttribute(FACEBOOK_ID, null);
                } else if (item.isPublishToFacebook() && hasFacebookPost(item)) {
                    facebookPageService.updateFacebookPost(
                            item.getAttribute(FACEBOOK_ID),
                            isBlank(item.getSocialSummary())
                                    ? item.getTitle()
                                    : item.getSocialSummary(),
                            item.getPublishDate().atZone(UTC).toLocalDateTime());
                }
            } else if (hasFacebookPost(item)) {
                facebookPageService.deletePost(item.getAttribute(FACEBOOK_ID));
                item.setAttribute(FACEBOOK_ID, null);
            }
        } catch (APIException ex) {
            throw new EntitySaveException(
                    "Error processing Facebook post", List.of(ex.getMessage()));
        }
    }

    private boolean hasFacebookPost(News item) {
        return isNotBlank(item.getAttribute(FACEBOOK_ID));
    }
}
