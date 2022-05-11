package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.exception.ResourceNotFoundException;
import cricket.merstham.website.frontend.model.News;
import cricket.merstham.website.frontend.model.datatables.SspGraphResponse;
import cricket.merstham.website.frontend.service.processors.ItemProcessor;
import cricket.merstham.website.graph.AdminNewsQuery;
import cricket.merstham.website.graph.DeleteNewsMutation;
import cricket.merstham.website.graph.GetNewsItemByPathQuery;
import cricket.merstham.website.graph.GetNewsItemQuery;
import cricket.merstham.website.graph.NewsFeedQuery;
import cricket.merstham.website.graph.SaveNewsAttributesMutation;
import cricket.merstham.website.graph.SaveNewsMutation;
import cricket.merstham.website.graph.type.NewsAttributeInput;
import cricket.merstham.website.graph.type.NewsInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class NewsService {

    private static final Logger LOG = LoggerFactory.getLogger(NewsService.class);
    private final GraphService graphService;
    private final List<ItemProcessor<News>> processors;

    @Autowired
    public NewsService(GraphService graphService, List<ItemProcessor<News>> processors) {
        this.graphService = graphService;
        this.processors = processors;
    }

    public SspGraphResponse<News> getItems(
            OAuth2AccessToken accessToken, int start, int length, String search)
            throws IOException {
        var query = new AdminNewsQuery(start, length, Input.optional(search));
        Response<AdminNewsQuery.Data> result = graphService.executeQuery(query, accessToken);
        var data = result.getData();
        return SspGraphResponse.<News>builder()
                .data(
                        data.news().stream()
                                .map(
                                        n ->
                                                News.builder()
                                                        .id(n.id())
                                                        .title(n.title())
                                                        .body(n.body())
                                                        .author(n.author())
                                                        .createdDate(n.createdDate())
                                                        .publishDate(n.publishDate())
                                                        .draft(n.draft())
                                                        .uuid(n.uuid())
                                                        .socialSummary(n.socialSummary())
                                                        .build())
                                .collect(Collectors.toList()))
                .recordsFiltered(data.newsTotals().totalMatching())
                .recordsTotal(data.newsTotals().totalRecords())
                .build();
    }

    public SspGraphResponse<News> feed(int page) throws IOException {
        var query = new NewsFeedQuery(page);
        Response<NewsFeedQuery.Data> result = graphService.executeQuery(query);
        return SspGraphResponse.<News>builder()
                .data(
                        result.getData().feed().stream()
                                .map(
                                        n ->
                                                News.builder()
                                                        .id(n.id())
                                                        .title(n.title())
                                                        .author(n.author())
                                                        .publishDate(n.publishDate())
                                                        .createdDate(n.createdDate())
                                                        .body(n.body())
                                                        .draft(n.draft())
                                                        .uuid(n.uuid())
                                                        .build())
                                .collect(Collectors.toList()))
                .recordsTotal(result.getData().newsTotals().totalRecords())
                .build();
    }

    public News saveNewsItem(OAuth2AccessToken accessToken, News news) throws IOException {
        var validationErrors =
                processors.stream()
                        .map(p -> p.preSave(news))
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
        if (!validationErrors.isEmpty()) {
            throw new EntitySaveException("Error saving News item", validationErrors);
        }
        var input =
                NewsInput.builder()
                        .id(news.getId())
                        .title(news.getTitle())
                        .author(news.getAuthor())
                        .body(news.getBody())
                        .socialSummary(news.getSocialSummary())
                        .createdDate(news.getCreatedDate())
                        .publishDate(news.getPublishDate())
                        .path(news.getLink().toString())
                        .draft(news.isDraft())
                        .uuid(news.getUuid())
                        .attributes(List.of())
                        .build();
        var saveRequest = SaveNewsMutation.builder().news(input).build();
        Response<SaveNewsMutation.Data> result =
                graphService.executeMutation(saveRequest, accessToken);
        if (result.hasErrors()) {
            result.getErrors().forEach(e -> LOG.error(e.getMessage()));
            throw new EntitySaveException(
                    "Error saving News item",
                    result.getErrors().stream()
                            .map(e -> e.getMessage())
                            .collect(Collectors.toList()));
        }
        news.setId(result.getData().saveNews().id());
        news.setAttributes(
                result.getData().saveNews().attributes().stream()
                        .collect(Collectors.toMap(o -> o.name(), o -> o.value())));

        processors.forEach(p -> p.postSave(news));
        var attributes =
                news.getAttributes().entrySet().stream()
                        .map(
                                e ->
                                        NewsAttributeInput.builder()
                                                .name(e.getKey())
                                                .value(e.getValue())
                                                .build())
                        .collect(Collectors.toList());
        var saveAttributesRequest =
                SaveNewsAttributesMutation.builder()
                        .id(news.getId())
                        .attributes(attributes)
                        .build();
        Response<SaveNewsAttributesMutation.Data> attributeResult =
                graphService.executeMutation(saveAttributesRequest, accessToken);
        if (attributeResult.hasErrors()) {
            attributeResult.getErrors().forEach(e -> LOG.error(e.getMessage()));
            throw new EntitySaveException(
                    "Error saving News item",
                    attributeResult.getErrors().stream()
                            .map(e -> e.getMessage())
                            .collect(Collectors.toList()));
        }
        var saved =
                News.builder()
                        .id(attributeResult.getData().saveNewsAttributes().id())
                        .title(attributeResult.getData().saveNewsAttributes().title())
                        .body(attributeResult.getData().saveNewsAttributes().body())
                        .publishDate(attributeResult.getData().saveNewsAttributes().publishDate())
                        .createdDate(attributeResult.getData().saveNewsAttributes().createdDate())
                        .author(attributeResult.getData().saveNewsAttributes().author())
                        .draft(attributeResult.getData().saveNewsAttributes().draft())
                        .uuid(attributeResult.getData().saveNewsAttributes().uuid())
                        .socialSummary(
                                attributeResult.getData().saveNewsAttributes().socialSummary())
                        .attributes(
                                attributeResult.getData().saveNewsAttributes().attributes().stream()
                                        .collect(Collectors.toMap(o -> o.name(), o -> o.value())))
                        .build();
        return saved;
    }

    public News get(OAuth2AccessToken accessToken, int id) throws IOException {
        var query = new GetNewsItemQuery(id);
        Response<GetNewsItemQuery.Data> news;
        if (isNull(accessToken)) {
            news = graphService.executeQuery(query);
        } else {
            news = graphService.executeQuery(query, accessToken);
        }
        if (isNull(news.getData().newsItem())) throw new ResourceNotFoundException();
        var result =
                News.builder()
                        .id(news.getData().newsItem().id())
                        .author(news.getData().newsItem().author())
                        .title(news.getData().newsItem().title())
                        .createdDate(news.getData().newsItem().createdDate())
                        .publishDate(news.getData().newsItem().publishDate())
                        .body(news.getData().newsItem().body())
                        .draft(news.getData().newsItem().draft())
                        .uuid(news.getData().newsItem().uuid())
                        .socialSummary(news.getData().newsItem().socialSummary())
                        .attributes(
                                news.getData().newsItem().attributes().stream()
                                        .collect(Collectors.toMap(a -> a.name(), a -> a.value())))
                        .build();
        processors.forEach(p -> p.postOpen(result));
        return result;
    }

    public News get(int id) throws IOException {
        return get(null, id);
    }

    public News get(String path) throws IOException {
        var query = new GetNewsItemByPathQuery(path);
        Response<GetNewsItemByPathQuery.Data> news = graphService.executeQuery(query);
        if (isNull(news.getData().newsItemByPath())) throw new ResourceNotFoundException();
        var result =
                News.builder()
                        .id(news.getData().newsItemByPath().id())
                        .author(news.getData().newsItemByPath().author())
                        .title(news.getData().newsItemByPath().title())
                        .createdDate(news.getData().newsItemByPath().createdDate())
                        .publishDate(news.getData().newsItemByPath().publishDate())
                        .body(news.getData().newsItemByPath().body())
                        .draft(news.getData().newsItemByPath().draft())
                        .uuid(news.getData().newsItemByPath().uuid())
                        .socialSummary(news.getData().newsItemByPath().socialSummary())
                        .attributes(
                                news.getData().newsItemByPath().attributes().stream()
                                        .collect(Collectors.toMap(a -> a.name(), a -> a.value())))
                        .build();
        processors.forEach(p -> p.postOpen(result));
        return result;
    }

    public boolean delete(OAuth2AccessToken accessToken, int id) throws IOException {
        var query = new DeleteNewsMutation(id);
        Response<DeleteNewsMutation.Data> result = graphService.executeMutation(query, accessToken);
        if (result.hasErrors()) {
            result.getErrors().forEach(e -> LOG.error(e.getMessage()));
        }
        return !result.hasErrors();
    }
}
