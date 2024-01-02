package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import static cricket.merstham.website.frontend.helpers.RoutesHelper.VENUE_ITEM_ROUTE;

@Controller
public class VenueController {
    private final VenueService service;

    @Autowired
    public VenueController(VenueService service) {
        this.service = service;
    }

    @GetMapping(value = VENUE_ITEM_ROUTE, name = "venue-item")
    public ModelAndView getItem(Principal principal, @PathVariable String slug) throws IOException {
        var venue = service.get(slug);
        return new ModelAndView("venue/item", Map.of("venue", venue));
    }
}
