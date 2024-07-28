package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import cricket.merstham.shared.dto.Fixture;
import cricket.merstham.shared.dto.News;
import cricket.merstham.shared.dto.StaticPage;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.exception.ResourceNotFoundException;
import cricket.merstham.website.frontend.model.datatables.SspGraphResponse;
import cricket.merstham.website.frontend.model.datatables.SspResponseDataWrapper;
import cricket.merstham.website.frontend.service.processors.ItemProcessor;
import cricket.merstham.website.graph.HomeQuery;
import cricket.merstham.website.graph.pages.AdminPagesQuery;
import cricket.merstham.website.graph.pages.DeletePageMutation;
import cricket.merstham.website.graph.pages.GetPageQuery;
import cricket.merstham.website.graph.pages.SavePageMutation;
import cricket.merstham.website.graph.type.PageInput;
import lombok.Builder;
import lombok.Getter;
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

import static cricket.merstham.website.frontend.helpers.GraphQLResultHelper.requireGraphData;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_PAGE_DELETE_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_PAGE_EDIT_ROUTE;
import static java.util.Objects.isNull;

@Service
public class PageService {

    private static final Logger LOG = LoggerFactory.getLogger(PageService.class);
    private final GraphService graphService;
    private final List<ItemProcessor<StaticPage>> processors;
    private final ModelMapper modelMapper;

    @Autowired
    public PageService(
            GraphService graphService,
            List<ItemProcessor<StaticPage>> processors,
            ModelMapper modelMapper) {
        this.graphService = graphService;
        this.processors = processors;
        this.modelMapper = modelMapper;
    }

    public SspGraphResponse<SspResponseDataWrapper<StaticPage>> getItems(
            OAuth2AccessToken accessToken, int start, int length, String search)
            throws IOException {
        var query = new AdminPagesQuery(start, length, Input.optional(search));
        Response<AdminPagesQuery.Data> result = graphService.executeQuery(query, accessToken);
        var data = result.getData();
        return SspGraphResponse.<SspResponseDataWrapper<StaticPage>>builder()
                .data(
                        data.getPages().stream()
                                .map(
                                        n ->
                                                SspResponseDataWrapper.<StaticPage>builder()
                                                        .data(modelMapper.map(n, StaticPage.class))
                                                        .editRouteTemplate(
                                                                Optional.of(ADMIN_PAGE_EDIT_ROUTE))
                                                        .deleteRouteTemplate(
                                                                Optional.of(
                                                                        ADMIN_PAGE_DELETE_ROUTE))
                                                        .mapFunction(
                                                                page ->
                                                                        Map.of(
                                                                                "slug",
                                                                                page.getSlug()))
                                                        .build())
                                .toList())
                .recordsFiltered(data.getPageTotals().getTotalMatching())
                .recordsTotal(data.getPageTotals().getTotalRecords())
                .build();
    }

    public StaticPage saveItem(OAuth2AccessToken accessToken, StaticPage page) throws IOException {
        var validationErrors =
                processors.stream().map(p -> p.preSave(page)).flatMap(List::stream).toList();
        if (!validationErrors.isEmpty()) {
            throw new EntitySaveException("Error saving page", validationErrors);
        }

        var input =
                PageInput.builder()
                        .slug(page.getSlug())
                        .title(page.getTitle())
                        .sortOrder(page.getSortOrder())
                        .content(page.getContent())
                        .menu(page.getMenu())
                        .build();
        var saveRequest = SavePageMutation.builder().page(input).build();
        Response<SavePageMutation.Data> result =
                graphService.executeMutation(saveRequest, accessToken);
        if (result.hasErrors()) {
            result.getErrors().forEach(e -> LOG.error(e.getMessage()));
            throw new EntitySaveException(
                    "Error saving page",
                    result.getErrors().stream().map(Error::getMessage).toList());
        }
        processors.forEach(p -> p.postSave(page));

        return modelMapper.map(result.getData().getSavePage(), StaticPage.class);
    }

    public StaticPage get(OAuth2AccessToken accessToken, String slug) throws IOException {
        var query = new GetPageQuery(slug);
        Response<GetPageQuery.Data> item;
        if (isNull(accessToken)) {
            item = graphService.executeQuery(query);
        } else {
            item = graphService.executeQuery(query, accessToken);
        }
        if (isNull(item.getData().getPage())) throw new ResourceNotFoundException();
        var result = modelMapper.map(item.getData().getPage(), StaticPage.class);
        processors.forEach(p -> p.postOpen(result));
        return result;
    }

    public StaticPage get(String slug) throws IOException {
        return get(null, slug);
    }

    public HomePage home() throws IOException {
        var query = new HomeQuery();
        Response<HomeQuery.Data> result = graphService.executeQuery(query);

        return HomePage.builder()
                .content(
                        modelMapper.map(
                                requireGraphData(
                                        result,
                                        HomeQuery.Data::getPage,
                                        () -> "Error getting page content"),
                                StaticPage.class))
                .topNews(
                        requireGraphData(
                                        result,
                                        HomeQuery.Data::getTopNews,
                                        () -> "Error getting top news")
                                .stream()
                                .map(n -> modelMapper.map(n, News.class))
                                .toList())
                .upcomingFixtures(
                        requireGraphData(
                                        result,
                                        HomeQuery.Data::getUpcomingFixtures,
                                        () -> "Error getting upcoming fixtures")
                                .stream()
                                .map(n -> modelMapper.map(n, Fixture.class))
                                .toList())
                .build();
    }

    public boolean delete(OAuth2AccessToken accessToken, String slug) throws IOException {
        var query = new DeletePageMutation(slug);
        Response<DeletePageMutation.Data> result = graphService.executeMutation(query, accessToken);
        if (result.hasErrors()) {
            result.getErrors().forEach(e -> LOG.error(e.getMessage()));
        }
        return !result.hasErrors();
    }

    public AboutPage about() throws IOException {
        var query = new AboutQuery();
        Response<AboutQuery.Data> response = graphService.executeQuery(query);
        return AboutPage.builder()
                .general(
                        modelMapper.map(
                                requireGraphData(response, AboutQuery.Data::getGeneral),
                                StaticPage.class))
                .success(
                        modelMapper.map(
                                requireGraphData(response, AboutQuery.Data::getSuccess),
                                StaticPage.class))
                .cricket(
                        modelMapper.map(
                                requireGraphData(response, AboutQuery.Data::getCricket),
                                StaticPage.class))
                .community(
                        modelMapper.map(
                                requireGraphData(response, AboutQuery.Data::getCommunity),
                                StaticPage.class))
                .members(requireGraphData(response, AboutQuery.Data::getMemberCount))
                .fixtures(requireGraphData(response, AboutQuery.Data::getFixtureCount))
                .wins(requireGraphData(response, AboutQuery.Data::getFixtureWinCount))
                .build();
    }

    @Builder
    @Getter
    public static class HomePage {
        private final StaticPage content;
        private final List<News> topNews;
        private final List<Fixture> upcomingFixtures;
    }

    @Builder
    @Getter
    public static class AboutPage {
        private final StaticPage general;
        private final StaticPage success;
        private final StaticPage cricket;
        private final StaticPage community;
        private final int members;
        private final int fixtures;
        private final int wins;
    }
}
