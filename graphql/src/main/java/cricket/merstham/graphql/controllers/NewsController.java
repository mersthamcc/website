package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.NewsService;
import cricket.merstham.shared.dto.KeyValuePair;
import cricket.merstham.shared.dto.News;
import cricket.merstham.shared.dto.Totals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class NewsController {

    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @QueryMapping
    public List<News> feed(@Argument("page") int page) {
        return newsService.getNewsFeed(page - 1);
    }

    @QueryMapping
    public Totals newsTotals(@Argument("searchString") String searchString) {
        return newsService.getNewsFeedTotals();
    }

    @QueryMapping
    public News newsItem(@Argument("id") int id) {
        return newsService.getNewsItemById(id);
    }

    @QueryMapping
    public News newsItemByPath(@Argument("path") String path) {
        return newsService.getNewsItemByPath(path);
    }

    @QueryMapping
    public List<News> news(
            @Argument("start") int start,
            @Argument("length") int length,
            @Argument("searchString") String searchString,
            Principal principal) {
        return newsService.getAdminNewsList(start, length, searchString);
    }

    @MutationMapping
    public News saveNews(@Argument("news") News news) {
        return newsService.save(news);
    }

    @MutationMapping
    public News saveNewsAttributes(
            @Argument("id") int id, @Argument("attributes") List<KeyValuePair> attributes) {
        return newsService.saveAttributes(id, attributes);
    }

    @MutationMapping
    public News deleteNews(@Argument("id") int id) {
        return newsService.delete(id);
    }
}
