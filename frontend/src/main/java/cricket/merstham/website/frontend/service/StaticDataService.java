package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import cricket.merstham.shared.dto.StaticData;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.exception.ResourceNotFoundException;
import cricket.merstham.website.frontend.model.datatables.SspGraphResponse;
import cricket.merstham.website.frontend.model.datatables.SspResponseDataWrapper;
import cricket.merstham.website.graph.pages.DeletePageMutation;
import cricket.merstham.website.graph.system.staticData.AdminStaticDataQuery;
import cricket.merstham.website.graph.system.staticData.DeleteStaticDataMutation;
import cricket.merstham.website.graph.system.staticData.SaveStaticDataMutation;
import cricket.merstham.website.graph.system.staticData.StaticDataByIdQuery;
import cricket.merstham.website.graph.system.staticData.StaticDataByPathQuery;
import cricket.merstham.website.graph.type.StaticDataInput;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static cricket.merstham.website.frontend.configuration.CacheConfiguration.STATIC_DATA_CACHE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_STATIC_DATA_DELETE_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_STATIC_DATA_EDIT_ROUTE;
import static java.util.Objects.isNull;

@Service
public class StaticDataService {

    private static final Logger LOG = LoggerFactory.getLogger(StaticDataService.class);
    private final GraphService graphService;
    private final ModelMapper modelMapper;

    @Autowired
    public StaticDataService(GraphService graphService, ModelMapper modelMapper) {
        this.graphService = graphService;
        this.modelMapper = modelMapper;
    }

    public SspGraphResponse<SspResponseDataWrapper<StaticData>> getItems(
            OAuth2AccessToken accessToken, int start, int length, String search)
            throws IOException {
        var query =
                AdminStaticDataQuery.builder().start(start).length(length).search(search).build();
        Response<AdminStaticDataQuery.Data> result = graphService.executeQuery(query, accessToken);
        var data = result.getData();
        return SspGraphResponse.<SspResponseDataWrapper<StaticData>>builder()
                .data(
                        data.getStaticData().stream()
                                .map(
                                        n ->
                                                SspResponseDataWrapper.<StaticData>builder()
                                                        .data(modelMapper.map(n, StaticData.class))
                                                        .editRouteTemplate(
                                                                Optional.of(
                                                                        ADMIN_STATIC_DATA_EDIT_ROUTE))
                                                        .deleteRouteTemplate(
                                                                Optional.of(
                                                                        ADMIN_STATIC_DATA_DELETE_ROUTE))
                                                        .mapFunction(d -> Map.of("id", d.getId()))
                                                        .build())
                                .toList())
                .recordsFiltered(data.getStaticDataTotals().getTotalMatching())
                .recordsTotal(data.getStaticDataTotals().getTotalRecords())
                .build();
    }

    @CacheEvict(cacheNames = STATIC_DATA_CACHE, allEntries = true)
    public StaticData saveItem(OAuth2AccessToken accessToken, StaticData data) throws IOException {
        var input =
                StaticDataInput.builder()
                        .id(data.getId())
                        .statusCode(data.getStatusCode())
                        .contentType(data.getContentType().trim())
                        .path(data.getPath().trim())
                        .content(data.getContent().trim())
                        .build();
        var saveRequest = SaveStaticDataMutation.builder().data(input).build();
        Response<SaveStaticDataMutation.Data> result =
                graphService.executeMutation(saveRequest, accessToken);
        if (result.hasErrors()) {
            result.getErrors().forEach(e -> LOG.error(e.getMessage()));
            throw new EntitySaveException(
                    "Error saving static data endpoint",
                    result.getErrors().stream().map(Error::getMessage).toList());
        }

        return modelMapper.map(result.getData().getSaveStaticData(), StaticData.class);
    }

    public StaticData get(OAuth2AccessToken accessToken, String path) throws IOException {
        var query = StaticDataByPathQuery.builder().path(path).build();
        Response<StaticDataByPathQuery.Data> item;
        if (isNull(accessToken)) {
            item = graphService.executeQuery(query);
        } else {
            item = graphService.executeQuery(query, accessToken);
        }
        if (isNull(item.getData().getStaticDataByPath())) throw new ResourceNotFoundException();
        var result = modelMapper.map(item.getData().getStaticDataByPath(), StaticData.class);
        return result;
    }

    public StaticData get(OAuth2AccessToken accessToken, int id) throws IOException {
        var query = StaticDataByIdQuery.builder().id(id).build();
        Response<StaticDataByIdQuery.Data> item;
        if (isNull(accessToken)) {
            item = graphService.executeQuery(query);
        } else {
            item = graphService.executeQuery(query, accessToken);
        }
        if (isNull(item.getData().getStaticDataById())) throw new ResourceNotFoundException();
        var result = modelMapper.map(item.getData().getStaticDataById(), StaticData.class);
        return result;
    }

    @Cacheable(cacheNames = STATIC_DATA_CACHE, key = "#path")
    public StaticData get(String path) throws IOException {
        return get(null, path);
    }

    @CacheEvict(cacheNames = STATIC_DATA_CACHE, allEntries = true)
    public boolean delete(OAuth2AccessToken accessToken, int id) throws IOException {
        var query = DeleteStaticDataMutation.builder().id(id).build();
        Response<DeletePageMutation.Data> result = graphService.executeMutation(query, accessToken);
        if (result.hasErrors()) {
            result.getErrors().forEach(e -> LOG.error(e.getMessage()));
        }
        return !result.hasErrors();
    }
}
