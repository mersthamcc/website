package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.views.FixtureCalendarFeedView;
import cricket.merstham.website.frontend.views.RssFeed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.Map;

@RestController
public class FeedController {

    private final RssFeed rssFeed;
    private final FixtureCalendarFeedView fixtureCalendarFeedView;

    @Autowired
    public FeedController(RssFeed rssFeed, FixtureCalendarFeedView fixtureCalendarFeedView) {
        this.rssFeed = rssFeed;
        this.fixtureCalendarFeedView = fixtureCalendarFeedView;
    }

    @GetMapping("/feeds/rss")
    public View getFeed() {
        return rssFeed;
    }

    @GetMapping("/feeds/rss/stripped")
    public View getStrippedFeed() {
        return getFeed();
    }

    @GetMapping("/feeds/fixtures/{id}.ical")
    public ModelAndView getFixtureFeed(@PathVariable("id") int teamId) {
        return new ModelAndView(fixtureCalendarFeedView, Map.of("teamId", teamId));
    }
}
