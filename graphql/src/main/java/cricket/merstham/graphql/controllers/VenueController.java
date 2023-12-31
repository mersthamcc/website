package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.VenueService;
import cricket.merstham.shared.dto.Totals;
import cricket.merstham.shared.dto.Venue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class VenueController {

    private final VenueService service;

    @Autowired
    public VenueController(VenueService service) {
        this.service = service;
    }

    @QueryMapping
    public Totals venueTotals(@Argument("searchString") String searchString) {
        return service.getPageTotals();
    }

    @QueryMapping
    public Venue venue(@Argument("slug") String slug) {
        return service.getEventItemById(slug);
    }

    @QueryMapping
    public List<Venue> venues(
            @Argument("start") int start,
            @Argument("length") int length,
            @Argument("searchString") String searchString,
            Principal principal) {
        return service.getAdminEntryList(start, length, searchString);
    }

    @QueryMapping
    public List<Venue> venuesForMenu() {
        return service.getVenuesForMenu();
    }

    @MutationMapping
    public Venue saveVenue(@Argument("venue") Venue venue) {
        return service.save(venue);
    }

    @MutationMapping
    public Venue deleteVenue(@Argument("slug") String slug) {
        return service.delete(slug);
    }
}
