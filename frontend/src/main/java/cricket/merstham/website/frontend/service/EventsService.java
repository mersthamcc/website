package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import cricket.merstham.shared.dto.Event;
import cricket.merstham.shared.dto.KeyValuePair;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.exception.ResourceNotFoundException;
import cricket.merstham.website.frontend.model.datatables.SspGraphResponse;
import cricket.merstham.website.frontend.model.datatables.SspResponseDataWrapper;
import cricket.merstham.website.frontend.service.processors.ItemProcessor;
import cricket.merstham.website.graph.events.AdminEventsQuery;
import cricket.merstham.website.graph.events.DeleteEventMutation;
import cricket.merstham.website.graph.events.FeedQuery;
import cricket.merstham.website.graph.events.GetEventItemByPathQuery;
import cricket.merstham.website.graph.events.GetEventItemQuery;
import cricket.merstham.website.graph.events.SaveEventAttributesMutation;
import cricket.merstham.website.graph.events.SaveEventMutation;
import cricket.merstham.website.graph.type.EventInput;
import cricket.merstham.website.graph.type.KeyValuePairInput;
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

import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_EVENT_DELETE_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_EVENT_EDIT_ROUTE;
import static java.util.Objects.isNull;

@Service
public class EventsService {

    private static final Logger LOG = LoggerFactory.getLogger(EventsService.class);
    private final GraphService graphService;
    private final List<ItemProcessor<Event>> processors;
    private final ModelMapper modelMapper;

    @Autowired
    public EventsService(
            GraphService graphService,
            List<ItemProcessor<Event>> processors,
            ModelMapper modelMapper) {
        this.graphService = graphService;
        this.processors = processors;
        this.modelMapper = modelMapper;
    }

    public SspGraphResponse<SspResponseDataWrapper<Event>> getItems(
            OAuth2AccessToken accessToken, int start, int length, String search)
            throws IOException {
        var query = new AdminEventsQuery(start, length, Input.optional(search));
        Response<AdminEventsQuery.Data> result = graphService.executeQuery(query, accessToken);
        var data = result.getData();
        return SspGraphResponse.<SspResponseDataWrapper<Event>>builder()
                .data(
                        data.getEvents().stream()
                                .map(
                                        n ->
                                                SspResponseDataWrapper.<Event>builder()
                                                        .data(modelMapper.map(n, Event.class))
                                                        .editRouteTemplate(
                                                                Optional.of(ADMIN_EVENT_EDIT_ROUTE))
                                                        .deleteRouteTemplate(
                                                                Optional.of(
                                                                        ADMIN_EVENT_DELETE_ROUTE))
                                                        .mapFunction(
                                                                news -> Map.of("id", news.getId()))
                                                        .build())
                                .toList())
                .recordsFiltered(data.getEventTotals().getTotalMatching())
                .recordsTotal(data.getEventTotals().getTotalRecords())
                .build();
    }

    public SspGraphResponse<Event> feed(int page) throws IOException {
        var query = new FeedQuery(page);
        Response<FeedQuery.Data> result = graphService.executeQuery(query);
        return SspGraphResponse.<Event>builder()
                .data(
                        result.getData().getEventsFeed().stream()
                                .map(n -> modelMapper.map(n, Event.class))
                                .toList())
                .recordsTotal(result.getData().getEventTotals().getTotalRecords())
                .build();
    }

    public Event saveEventItem(OAuth2AccessToken accessToken, Event event) throws IOException {
        var validationErrors =
                processors.stream().map(p -> p.preSave(event)).flatMap(List::stream).toList();
        if (!validationErrors.isEmpty()) {
            throw new EntitySaveException("Error saving Event", validationErrors);
        }

        var input =
                EventInput.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .body(event.getBody())
                        .eventDate(event.getEventDate())
                        .path(event.getPath())
                        .uuid(event.getUuid())
                        .location(event.getLocation())
                        .callToActionDescription(event.getCallToActionDescription())
                        .callToActionLink(event.getCallToActionLink().toString())
                        .banner(event.getBanner())
                        .attributes(List.of())
                        .build();
        var saveRequest = SaveEventMutation.builder().event(input).build();
        Response<SaveEventMutation.Data> result =
                graphService.executeMutation(saveRequest, accessToken);
        if (result.hasErrors()) {
            result.getErrors().forEach(e -> LOG.error(e.getMessage()));
            throw new EntitySaveException(
                    "Error saving Event",
                    result.getErrors().stream().map(Error::getMessage).toList());
        }
        event.setId(result.getData().getSaveEvent().getId());
        event.setAttributes(
                result.getData().getSaveEvent().getAttributes().stream()
                        .map(
                                a ->
                                        KeyValuePair.builder()
                                                .key(a.getKey())
                                                .value(a.getValue())
                                                .build())
                        .toList());

        processors.forEach(p -> p.postSave(event));

        return saveAttributes(accessToken, event);
    }

    public Event saveAttributes(OAuth2AccessToken accessToken, Event event) throws IOException {
        var saveAttributesRequest =
                SaveEventAttributesMutation.builder()
                        .id(event.getId())
                        .attributes(
                                event.getAttributes().stream()
                                        .map(
                                                a ->
                                                        KeyValuePairInput.builder()
                                                                .key(a.getKey())
                                                                .value(a.getValue())
                                                                .build())
                                        .toList())
                        .build();
        Response<SaveEventAttributesMutation.Data> attributeResult =
                graphService.executeMutation(saveAttributesRequest, accessToken);
        if (attributeResult.hasErrors() || isNull(attributeResult.getData())) {
            attributeResult.getErrors().forEach(e -> LOG.error(e.getMessage()));
            throw new EntitySaveException(
                    "Error saving Event item",
                    attributeResult.getErrors().stream().map(Error::getMessage).toList());
        }
        return modelMapper.map(attributeResult.getData().getSaveEventAttributes(), Event.class);
    }

    public Event get(OAuth2AccessToken accessToken, int id) throws IOException {
        var query = new GetEventItemQuery(id);
        Response<GetEventItemQuery.Data> event;
        if (isNull(accessToken)) {
            event = graphService.executeQuery(query);
        } else {
            event = graphService.executeQuery(query, accessToken);
        }
        if (isNull(event.getData().getEventItem())) throw new ResourceNotFoundException();
        var result = modelMapper.map(event.getData().getEventItem(), Event.class);
        processors.forEach(p -> p.postOpen(result));
        return result;
    }

    public Event get(int id) throws IOException {
        return get(null, id);
    }

    public Event get(String path) throws IOException {
        var query = new GetEventItemByPathQuery(path);
        Response<GetEventItemByPathQuery.Data> news = graphService.executeQuery(query);
        if (isNull(news.getData().getEventItemByPath())) throw new ResourceNotFoundException();
        var result = modelMapper.map(news.getData().getEventItemByPath(), Event.class);
        processors.forEach(p -> p.postOpen(result));
        return result;
    }

    public boolean delete(OAuth2AccessToken accessToken, int id) throws IOException {
        var query = new DeleteEventMutation(id);
        Response<DeleteEventMutation.Data> result =
                graphService.executeMutation(query, accessToken);
        if (result.hasErrors()) {
            result.getErrors().forEach(e -> LOG.error(e.getMessage()));
        }
        return !result.hasErrors();
    }
}
