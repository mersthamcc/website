package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.service.EventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import static cricket.merstham.website.frontend.helpers.RoleHelper.EVENTS;
import static cricket.merstham.website.frontend.helpers.RoleHelper.hasRole;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.EVENTS_HOME_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.EVENTS_ITEM_LEGACY_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.EVENTS_ITEM_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.EVENTS_ROUTE_TEMPLATE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.buildRoute;

@Controller
public class EventsController {
    private final EventsService service;

    @Autowired
    public EventsController(EventsService service) {
        this.service = service;
    }

    @GetMapping(value = EVENTS_HOME_ROUTE, name = "events")
    public ModelAndView home(@RequestParam(name = "page", defaultValue = "1") int page)
            throws IOException {
        var events = service.feed(page);
        return new ModelAndView(
                "events/home",
                Map.of(
                        "events",
                        events.getData(),
                        "totalPages",
                        Math.floorDiv(events.getRecordsTotal(), 10) + 1,
                        "page",
                        page));
    }

    @GetMapping(value = EVENTS_ITEM_LEGACY_ROUTE, name = "events-item-legacy")
    public RedirectView legacyRedirect(@PathVariable("id") int id) throws IOException {
        return new RedirectView(service.get(id).getPath().toString());
    }

    @GetMapping(value = EVENTS_ITEM_ROUTE, name = "events-item")
    public ModelAndView getItem(
            Principal principal,
            @PathVariable String year,
            @PathVariable String month,
            @PathVariable String day,
            @PathVariable String slug)
            throws IOException {
        var event =
                service.get(
                        buildRoute(
                                        EVENTS_ROUTE_TEMPLATE,
                                        Map.of(
                                                "year", year,
                                                "month", month,
                                                "day", day,
                                                "slug", slug))
                                .toString());
        return new ModelAndView("events/item", Map.of("event", event));
    }

    private boolean isAdmin(Principal principal) {
        return hasRole(principal, EVENTS);
    }
}
