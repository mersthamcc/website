package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.model.admintables.News;
import cricket.merstham.website.frontend.model.datatables.SspGraphResponse;
import cricket.merstham.website.graph.AdminNewsQuery;
import cricket.merstham.website.graph.DeleteNewsMutation;
import cricket.merstham.website.graph.GetNewsItemQuery;
import cricket.merstham.website.graph.SaveNewsMutation;
import cricket.merstham.website.graph.type.NewsInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private static final Logger LOG = LoggerFactory.getLogger(NewsService.class);
    private final GraphService graphService;

    @Autowired
    public NewsService(GraphService graphService) {
        this.graphService = graphService;
    }

    public SspGraphResponse<News> getItems(Principal principal, int start, int length, String search) throws IOException {
        var query = new AdminNewsQuery(start, length, Input.optional(search));
        Response<AdminNewsQuery.Data> result = graphService.executeQuery(query, principal);
        var data = result.getData();
        return SspGraphResponse.<News>builder()
                .data(data.news().stream().map(n -> News
                        .builder()
                        .id(n.id())
                        .title(n.title())
                        .body(n.body())
                        .author(n.author())
                        .createdDate(n.createdDate())
                        .publishDate(n.publishDate())
                        .build()
                ).collect(Collectors.toList()))
                .recordsFiltered(data.newsTotals().totalMatching())
                .recordsTotal(data.newsTotals().totalRecords())
                .build();
    }

    public News saveNewsItem(Principal principal, News news) throws IOException {
        news.setPublishDate(LocalDateTime.now());
        news.setCreatedDate(LocalDateTime.now());
        var input = NewsInput.builder()
                .id(news.getId())
                .title(news.getTitle())
                .author(news.getAuthor())
                .body(news.getBody())
                .createdDate(news.getCreatedDate())
                .publishDate(news.getPublishDate())
                .path(news.getLink().toString())
                .build();
        var saveRequest = SaveNewsMutation.builder().news(input).build();
        Response<SaveNewsMutation.Data> result = graphService.executeMutation(saveRequest, principal);
        if (result.hasErrors()) {
            result.getErrors().forEach(e -> LOG.error(e.getMessage()));
            throw new EntitySaveException(
                    "Error saving News item",
                    result.getErrors().stream().map(e -> e.getMessage()).collect(Collectors.toList()));
        }
        return News.builder()
                .id(result.getData().saveNews().id())
                .title(result.getData().saveNews().title())
                .body(result.getData().saveNews().body())
                .publishDate(result.getData().saveNews().publishDate())
                .createdDate(result.getData().saveNews(). createdDate())
                .author(result.getData().saveNews().author())
                .build();
    }

    public News get(Principal principal, int id) throws IOException {
        var query = new GetNewsItemQuery(id);
        Response<GetNewsItemQuery.Data> news = graphService.executeQuery(query, principal);
        return News.builder()
                .id(news.getData().newsItem().id())
                .author(news.getData().newsItem().author())
                .title(news.getData().newsItem().title())
                .createdDate(news.getData().newsItem().createdDate())
                .publishDate(news.getData().newsItem().publishDate())
                .body(news.getData().newsItem().body())
                .build();
    }

    public boolean delete(Principal principal, int id) throws IOException {
        var query = new DeleteNewsMutation(id);
        Response<DeleteNewsMutation.Data> result = graphService.executeMutation(query, principal);
        if (result.hasErrors()) {
            result.getErrors().forEach(e -> LOG.error(e.getMessage()));
        }
        return !result.hasErrors();
    }
}
