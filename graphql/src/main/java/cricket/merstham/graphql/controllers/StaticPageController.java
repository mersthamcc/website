package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.StaticPageService;
import cricket.merstham.shared.dto.StaticPage;
import cricket.merstham.shared.dto.Totals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class StaticPageController {

    private final StaticPageService service;

    @Autowired
    public StaticPageController(StaticPageService service) {
        this.service = service;
    }

    @QueryMapping
    public Totals pageTotals(@Argument("searchString") String searchString) {
        return service.getPageTotals();
    }

    @QueryMapping
    public StaticPage page(@Argument("slug") String slug) {
        return service.getEventItemById(slug);
    }

    @QueryMapping
    public List<StaticPage> pages(
            @Argument("start") int start,
            @Argument("length") int length,
            @Argument("searchString") String searchString,
            Principal principal) {
        return service.getAdminEntryList(start, length, searchString);
    }

    @MutationMapping
    public StaticPage savePage(@Argument("page") StaticPage page) {
        return service.save(page);
    }

    @MutationMapping
    public StaticPage deletePage(@Argument("slug") String slug) {
        return service.delete(slug);
    }
}
