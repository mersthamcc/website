package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.EventsService;
import cricket.merstham.shared.dto.Event;
import cricket.merstham.shared.dto.KeyValuePair;
import cricket.merstham.shared.dto.Totals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class EventsController {

    private final EventsService service;

    @Autowired
    public EventsController(EventsService service) {
        this.service = service;
    }

    @QueryMapping
    public List<Event> eventsFeed(@Argument("page") int page) {
        return service.getEventsFeed(page - 1);
    }

    @QueryMapping
    public Totals eventTotals(@Argument("searchString") String searchString) {
        return service.getEventFeedTotals();
    }

    @QueryMapping
    public Event eventItem(@Argument("id") int id) {
        return service.getEventItemById(id);
    }

    @QueryMapping
    public Event eventItemByPath(@Argument("path") String path) {
        return service.getEventItemByPath(path);
    }

    @QueryMapping
    public List<Event> events(
            @Argument("start") int start,
            @Argument("length") int length,
            @Argument("searchString") String searchString,
            Principal principal) {
        return service.getAdminEntryList(start, length, searchString);
    }

    @MutationMapping
    public Event saveEvent(@Argument("event") Event event) {
        return service.save(event);
    }

    @MutationMapping
    public Event saveEventAttributes(
            @Argument("id") int id, @Argument("attributes") List<KeyValuePair> attributes) {
        return service.saveAttributes(id, attributes);
    }

    @MutationMapping
    public Event deleteEvent(@Argument("id") int id) {
        return service.delete(id);
    }
}
