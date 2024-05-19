package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import static cricket.merstham.website.frontend.helpers.RoutesHelper.PAGES_ITEM_ROUTE;

@Controller
public class PageController {
    private final PageService service;

    @Autowired
    public PageController(PageService service) {
        this.service = service;
    }

    @GetMapping(value = PAGES_ITEM_ROUTE, name = "pages-item")
    public ModelAndView getItem(Principal principal, @PathVariable String slug) throws IOException {
        var page = service.get(slug);
        return new ModelAndView("page/item", Map.of("page", page, "pageTitle", page.getTitle()));
    }
}
