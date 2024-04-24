package cricket.merstham.website.frontend.views;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Enclosure;
import com.rometools.rome.feed.rss.Item;
import cricket.merstham.shared.dto.News;
import cricket.merstham.website.frontend.service.NewsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static cricket.merstham.website.frontend.helpers.UriHelper.resolveUrl;

@Component
public class RssFeed extends AbstractRssFeedView {

    private final NewsService service;
    private final String clubName;
    private final String feedDescription;
    private final String baseUrl;
    private final String resourcesUrl;

    public RssFeed(
            NewsService service,
            @Value("${club.club-name}") String clubName,
            @Value("${club.rss.feed-description}") String feedDescription,
            @Value("${base-url}") String baseUrl,
            @Value("${resources.base-url}") String resourcesUrl) {
        this.service = service;
        this.clubName = clubName;
        this.feedDescription = feedDescription;
        this.baseUrl = baseUrl;
        this.resourcesUrl = resourcesUrl;
    }

    @Override
    protected void buildFeedMetadata(
            Map<String, Object> model, Channel feed, HttpServletRequest request) {
        super.buildFeedMetadata(model, feed, request);
        feed.setTitle(clubName);
        feed.setDescription(feedDescription);
        feed.setLink(baseUrl);
        feed.setPubDate(new Date());
    }

    @Override
    protected List<Item> buildFeedItems(
            Map<String, Object> model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.addHeader("Access-Control-Allow-Origin", "*");
        return service.feed(1).getData().stream().map(this::mapToItem).toList();
    }

    private Item mapToItem(News news) {
        var item = new Item();
        item.setTitle(news.getTitle());
        item.setPubDate(Date.from(news.getCreatedDate()));
        var description = new Description();
        description.setValue(news.getAbstract());
        item.setDescription(description);
        item.setLink(resolveUrl(resolveUrl(baseUrl, "/news/"), news.getPath()));
        if (news.hasImages()) {
            news.getImages().stream()
                    .findFirst()
                    .ifPresent(
                            image -> {
                                var enclosure = new Enclosure();
                                enclosure.setUrl(resolveUrl(resourcesUrl, image.getPath()));
                                item.setEnclosures(List.of(enclosure));
                            });
        }
        return item;
    }
}
