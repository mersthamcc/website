package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.entity.NewsEntity;
import cricket.merstham.graphql.services.NewsService;
import cricket.merstham.shared.dto.News;
import cricket.merstham.shared.dto.NewsAttribute;
import cricket.merstham.shared.dto.Totals;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class NewsController {

    private final NewsService newsService;

    private final ModelMapper modelMapper;

    @Autowired
    public NewsController(NewsService newsService, ModelMapper modelMapper) {
        this.newsService = newsService;
        this.modelMapper = modelMapper;
    }

    @QueryMapping
    public List<News> feed(@Argument("page") int page) {
        var news = newsService.getNewsFeed(page - 1);
        return news.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @QueryMapping
    public Totals newsTotals(@Argument("searchString") String searchString) {
        return newsService.getNewsFeedTotals();
    }

    @QueryMapping
    public News newsItem(@Argument("id") int id) {
        return convertToDto(newsService.getNewsItemById(id));
    }

    @QueryMapping
    public News newsItemByPath(@Argument("path") String path) {
        return convertToDto(newsService.getNewsItemByPath(path));
    }

    @QueryMapping
    public List<News> news(
            @Argument("start") int start,
            @Argument("length") int length,
            @Argument("searchString") String searchString,
            Principal principal) {
        var news = newsService.getAdminNewsList(start, length, searchString);
        return news.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @MutationMapping
    public News saveNews(@Argument("news") News news) {
        return convertToDto(newsService.save(news));
    }

    @MutationMapping
    public News saveNewsAttributes(
            @Argument("id") int id, @Argument("attributes") List<NewsAttribute> attributes) {
        return convertToDto(newsService.saveAttributes(id, attributes));
    }

    @MutationMapping
    public News deleteNews(@Argument("id") int id) {
        return convertToDto(newsService.delete(id));
    }

    private News convertToDto(NewsEntity news) {
        return modelMapper.map(news, News.class);
    }
}
