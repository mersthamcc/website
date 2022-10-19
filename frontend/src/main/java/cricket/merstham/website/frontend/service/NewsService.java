package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import cricket.merstham.shared.dto.KeyValuePair;
import cricket.merstham.shared.dto.News;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.exception.ResourceNotFoundException;
import cricket.merstham.website.frontend.model.datatables.SspGraphResponse;
import cricket.merstham.website.frontend.model.datatables.SspResponseDataWrapper;
import cricket.merstham.website.frontend.service.processors.ItemProcessor;
import cricket.merstham.website.graph.AdminNewsQuery;
import cricket.merstham.website.graph.DeleteNewsMutation;
import cricket.merstham.website.graph.GetNewsItemByPathQuery;
import cricket.merstham.website.graph.GetNewsItemQuery;
import cricket.merstham.website.graph.NewsFeedQuery;
import cricket.merstham.website.graph.SaveNewsAttributesMutation;
import cricket.merstham.website.graph.SaveNewsMutation;
import cricket.merstham.website.graph.type.KeyValuePairInput;
import cricket.merstham.website.graph.type.NewsInput;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_NEWS_DELETE_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_NEWS_EDIT_ROUTE;
import static java.util.Objects.isNull;

@Service
public class NewsService {

    private static final Logger LOG = LoggerFactory.getLogger(NewsService.class);
    private final GraphService graphService;
    private final List<ItemProcessor<News>> processors;
    private final ModelMapper modelMapper;

    @Autowired
    public NewsService(
            GraphService graphService,
            List<ItemProcessor<News>> processors,
            ModelMapper modelMapper) {
        this.graphService = graphService;
        this.processors = processors;
        this.modelMapper = modelMapper;
    }

    public SspGraphResponse<SspResponseDataWrapper<News>> getItems(
            OAuth2AccessToken accessToken, int start, int length, String search)
            throws IOException {
        var query = new AdminNewsQuery(start, length, Input.optional(search));
        Response<AdminNewsQuery.Data> result = graphService.executeQuery(query, accessToken);
        var data = result.getData();
        return SspGraphResponse.<SspResponseDataWrapper<News>>builder()
                .data(
                        data.getNews().stream()
                                .map(
                                        n ->
                                                SspResponseDataWrapper.<News>builder()
                                                        .data(modelMapper.map(n, News.class))
                                                        .editRouteTemplate(
                                                                Optional.of(ADMIN_NEWS_EDIT_ROUTE))
                                                        .deleteRouteTemplate(
                                                                Optional.of(
                                                                        ADMIN_NEWS_DELETE_ROUTE))
                                                        .mapFunction(
                                                                news -> Map.of("id", news.getId()))
                                                        .build())
                                .collect(Collectors.toList()))
                .recordsFiltered(data.getNewsTotals().getTotalMatching())
                .recordsTotal(data.getNewsTotals().getTotalRecords())
                .build();
    }

    public SspGraphResponse<News> feed(int page) throws IOException {
        var query = new NewsFeedQuery(page);
        Response<NewsFeedQuery.Data> result = graphService.executeQuery(query);
        return SspGraphResponse.<News>builder()
                .data(
                        result.getData().getFeed().stream()
                                .map(n -> modelMapper.map(n, News.class))
                                .collect(Collectors.toList()))
                .recordsTotal(result.getData().getNewsTotals().getTotalRecords())
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
                        .path(news.getPath().toString())
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
        news.setId(result.getData().getSaveNews().getId());
        news.setAttributes(
                result.getData().getSaveNews().getAttributes().stream()
                        .map(
                                a ->
                                        KeyValuePair.builder()
                                                .key(a.getKey())
                                                .value(a.getValue())
                                                .build())
                        .collect(Collectors.toList()));

        processors.forEach(p -> p.postSave(news));

        return saveAttributes(accessToken, news);
    }

    public News saveAttributes(OAuth2AccessToken accessToken, News news) throws IOException {
        var saveAttributesRequest =
                SaveNewsAttributesMutation.builder()
                        .id(news.getId())
                        .attributes(
                                news.getAttributes().stream()
                                        .map(
                                                a ->
                                                        KeyValuePairInput.builder()
                                                                .key(a.getKey())
                                                                .value(a.getValue())
                                                                .build())
                                        .collect(Collectors.toList()))
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
        return modelMapper.map(attributeResult.getData().getSaveNewsAttributes(), News.class);
    }

    public News get(OAuth2AccessToken accessToken, int id) throws IOException {
        var query = new GetNewsItemQuery(id);
        Response<GetNewsItemQuery.Data> news;
        if (isNull(accessToken)) {
            news = graphService.executeQuery(query);
        } else {
            news = graphService.executeQuery(query, accessToken);
        }
        if (isNull(news.getData().getNewsItem())) throw new ResourceNotFoundException();
        var result = modelMapper.map(news.getData().getNewsItem(), News.class);
        processors.forEach(p -> p.postOpen(result));
        return result;
    }

    public News get(int id) throws IOException {
        return get(null, id);
    }

    public News get(String path) throws IOException {
        var query = new GetNewsItemByPathQuery(path);
        Response<GetNewsItemByPathQuery.Data> news = graphService.executeQuery(query);
        if (isNull(news.getData().getNewsItemByPath())) throw new ResourceNotFoundException();
        var result = modelMapper.map(news.getData().getNewsItemByPath(), News.class);
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
