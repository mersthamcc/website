package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import cricket.merstham.shared.dto.Venue;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.exception.ResourceNotFoundException;
import cricket.merstham.website.frontend.model.datatables.SspGraphResponse;
import cricket.merstham.website.frontend.model.datatables.SspResponseDataWrapper;
import cricket.merstham.website.frontend.service.processors.ItemProcessor;
import cricket.merstham.website.graph.type.VenueInput;
import cricket.merstham.website.graph.venues.AdminVenueQuery;
import cricket.merstham.website.graph.venues.DeleteVenueMutation;
import cricket.merstham.website.graph.venues.GetVenueQuery;
import cricket.merstham.website.graph.venues.SaveVenueMutation;
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

import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_VENUE_DELETE_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_VENUE_EDIT_ROUTE;
import static java.util.Objects.isNull;

@Service
public class VenueService {

    private static final Logger LOG = LoggerFactory.getLogger(VenueService.class);
    private final GraphService graphService;
    private final List<ItemProcessor<Venue>> processors;
    private final ModelMapper modelMapper;

    @Autowired
    public VenueService(
            GraphService graphService,
            List<ItemProcessor<Venue>> processors,
            ModelMapper modelMapper) {
        this.graphService = graphService;
        this.processors = processors;
        this.modelMapper = modelMapper;
    }

    public SspGraphResponse<SspResponseDataWrapper<Venue>> getItems(
            OAuth2AccessToken accessToken, int start, int length, String search)
            throws IOException {
        var query = new AdminVenueQuery(start, length, Input.optional(search));
        Response<AdminVenueQuery.Data> result = graphService.executeQuery(query, accessToken);
        var data = result.getData();
        return SspGraphResponse.<SspResponseDataWrapper<Venue>>builder()
                .data(
                        data.getVenues().stream()
                                .map(
                                        n ->
                                                SspResponseDataWrapper.<Venue>builder()
                                                        .data(modelMapper.map(n, Venue.class))
                                                        .editRouteTemplate(
                                                                Optional.of(ADMIN_VENUE_EDIT_ROUTE))
                                                        .deleteRouteTemplate(
                                                                Optional.of(
                                                                        ADMIN_VENUE_DELETE_ROUTE))
                                                        .mapFunction(
                                                                page ->
                                                                        Map.of(
                                                                                "slug",
                                                                                page.getSlug()))
                                                        .build())
                                .toList())
                .recordsFiltered(data.getVenueTotals().getTotalMatching())
                .recordsTotal(data.getVenueTotals().getTotalRecords())
                .build();
    }

    public Venue saveItem(OAuth2AccessToken accessToken, Venue venue) throws IOException {
        var validationErrors =
                processors.stream().map(p -> p.preSave(venue)).flatMap(List::stream).toList();
        if (!validationErrors.isEmpty()) {
            throw new EntitySaveException("Error saving venue", validationErrors);
        }

        var input =
                VenueInput.builder()
                        .slug(venue.getSlug())
                        .name(venue.getName())
                        .sortOrder(venue.getSortOrder())
                        .description(venue.getDescription())
                        .directions(venue.getDirections())
                        .address(venue.getAddress())
                        .postCode(venue.getPostCode())
                        .aliasFor(venue.getAliasFor())
                        .showOnMenu(venue.isShowOnMenu())
                        .latitude(venue.getLatitude().doubleValue())
                        .longitude(venue.getLongitude().doubleValue())
                        .marker(venue.getMarker())
                        .build();
        var saveRequest = SaveVenueMutation.builder().venue(input).build();
        Response<SaveVenueMutation.Data> result =
                graphService.executeMutation(saveRequest, accessToken);
        if (result.hasErrors()) {
            result.getErrors().forEach(e -> LOG.error(e.getMessage()));
            throw new EntitySaveException(
                    "Error saving venue",
                    result.getErrors().stream().map(Error::getMessage).toList());
        }
        processors.forEach(p -> p.postSave(venue));

        return modelMapper.map(result.getData().getSaveVenue(), Venue.class);
    }

    public Venue get(OAuth2AccessToken accessToken, String slug) throws IOException {
        var query = new GetVenueQuery(slug);
        Response<GetVenueQuery.Data> item;
        if (isNull(accessToken)) {
            item = graphService.executeQuery(query);
        } else {
            item = graphService.executeQuery(query, accessToken);
        }
        if (isNull(item.getData().getVenue())) throw new ResourceNotFoundException();
        var result = modelMapper.map(item.getData().getVenue(), Venue.class);
        processors.forEach(p -> p.postOpen(result));
        return result;
    }

    public Venue get(String slug) throws IOException {
        return get(null, slug);
    }

    public boolean delete(OAuth2AccessToken accessToken, String slug) throws IOException {
        var query = new DeleteVenueMutation(slug);
        Response<DeleteVenueMutation.Data> result =
                graphService.executeMutation(query, accessToken);
        if (result.hasErrors()) {
            result.getErrors().forEach(e -> LOG.error(e.getMessage()));
        }
        return !result.hasErrors();
    }
}
