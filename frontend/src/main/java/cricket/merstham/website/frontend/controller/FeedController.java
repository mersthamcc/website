package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.views.RssFeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

@RestController
public class FeedController {

    private final RssFeed rssFeed;

    @Autowired
    public FeedController(RssFeed rssFeed) {
        this.rssFeed = rssFeed;
    }

    @GetMapping("/feeds/rss")
    public View getFeed() {
        return rssFeed;
    }

    @GetMapping("/feeds/rss/stripped")
    public View getStrippedFeed() {
        return rssFeed;
    }
}
