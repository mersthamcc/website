package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.exception.ResourceNotFoundException;
import cricket.merstham.website.frontend.service.NewsService;
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

import static cricket.merstham.website.frontend.helpers.RoleHelper.NEWS;
import static cricket.merstham.website.frontend.helpers.RoleHelper.hasRole;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.NEWS_HOME_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.NEWS_ITEM_LEGACY_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.NEWS_ITEM_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.NEWS_ROUTE_TEMPLATE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.buildRoute;

@Controller("NewsController")
public class NewsController {
    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping(value = NEWS_HOME_ROUTE, name = "news")
    public ModelAndView home(@RequestParam(name = "page", defaultValue = "1") int page)
            throws IOException {
        var news = newsService.feed(page);
        return new ModelAndView(
                "news/home",
                Map.of(
                        "news",
                        news.getData(),
                        "totalPages",
                        Math.floorDiv(news.getRecordsTotal(), 10) + 1,
                        "page",
                        page));
    }

    @GetMapping(value = NEWS_ITEM_LEGACY_ROUTE, name = "news-item-legacy")
    public RedirectView legacyRedirect(Principal principal, @PathVariable("id") int id)
            throws IOException {
        return new RedirectView(newsService.get(id).getPath().toString());
    }

    @GetMapping(value = NEWS_ITEM_ROUTE, name = "news")
    public ModelAndView getItem(
            Principal principal,
            @PathVariable String year,
            @PathVariable String month,
            @PathVariable String day,
            @PathVariable String slug)
            throws IOException {
        var news =
                newsService.get(
                        buildRoute(
                                        NEWS_ROUTE_TEMPLATE,
                                        Map.of(
                                                "year", year,
                                                "month", month,
                                                "day", day,
                                                "slug", slug))
                                .toString());
        if (news.isDraft() && !isAdmin(principal)) {
            throw new ResourceNotFoundException();
        }
        return new ModelAndView("news/item", Map.of("news", news));
    }

    private boolean isAdmin(Principal principal) {
        return hasRole(principal, NEWS);
    }
}
