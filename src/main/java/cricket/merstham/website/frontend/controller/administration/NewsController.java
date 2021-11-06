package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.helpers.UserHelper;
import cricket.merstham.website.frontend.model.DataTableColumn;
import cricket.merstham.website.frontend.model.admintables.News;
import cricket.merstham.website.frontend.model.datatables.SspRequest;
import cricket.merstham.website.frontend.model.datatables.SspResponse;
import cricket.merstham.website.frontend.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_NEWS_AJAX_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_NEWS_BASE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_NEWS_EDIT_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_NEWS_NEW_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_NEWS_SAVE_ROUTE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller("AdminNewsController")
public class NewsController extends SspController<News> {
    private static final Logger LOG = LoggerFactory.getLogger(NewsController.class);

    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping(value = ADMIN_NEWS_BASE, name = "admin-news-list")
    @PreAuthorize("hasRole('ROLE_NEWS')")
    public ModelAndView list(Principal principal) {
        return new ModelAndView(
                "administration/news/list",
                Map.of(
                        "newsColumns",
                        List.of(
                                new DataTableColumn()
                                        .setKey("news.title")
                                        .setFieldName("title"),
                                new DataTableColumn()
                                        .setKey("news.publish-date")
                                        .setFieldName("publishDate"))));
    }

    @GetMapping(value = ADMIN_NEWS_NEW_ROUTE, name = "admin-news-new")
    @PreAuthorize("hasRole('ROLE_NEWS')")
    public ModelAndView newPost(HttpServletRequest request, Principal principal) {
        var flash = RequestContextUtils.getInputFlashMap(request);
        if (flash.isEmpty()) {
            var news = News.builder()
                    .title("")
                    .author(UserHelper.getUserFullName(principal))
                    .body("")
                    .build();
            return new ModelAndView("administration/news/edit", Map.of(
                    "news", news
            ));
        } else {
            return new ModelAndView("administration/news/edit", Map.of(
                    "news", flash.get("news"),
                    "errors", flash.get("errors")
            ));
        }

    }

    @GetMapping(value = ADMIN_NEWS_EDIT_ROUTE, name = "admin-news-edit")
    @PreAuthorize("hasRole('ROLE_NEWS')")
    public ModelAndView editPost(Principal principal, @PathVariable("id") int id) throws IOException {
        News news = newsService.get(principal, id);
        return new ModelAndView("administration/news/edit", Map.of(
                "news", news
        ));
    }

    @PostMapping(value = ADMIN_NEWS_SAVE_ROUTE, name = "admin-news-save")
    @PreAuthorize("hasRole('ROLE_NEWS')")
    public RedirectView newPost(Principal principal, News news, RedirectAttributes redirectAttributes) throws IOException {
        try {
            newsService.saveNewsItem(principal, news);
            return new RedirectView(ADMIN_NEWS_BASE);
        } catch (EntitySaveException ex) {
            redirectAttributes.addFlashAttribute("errors", ex.getErrors());
            redirectAttributes.addFlashAttribute("news", news);
            return new RedirectView(ADMIN_NEWS_NEW_ROUTE);
        }
    }

    @Override
    @PostMapping(
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE,
            path = ADMIN_NEWS_AJAX_ROUTE)
    public @ResponseBody SspResponse<News> getData(Principal principal, @RequestBody SspRequest request) {
        try {
            var data = newsService.getItems(
                    principal,
                    request.getStart(),
                    request.getLength(),
                    request.getSearch().getValue());
            return SspResponse.builder(News.class)
                    .withDraw(request.getDraw())
                    .withData(data.getNews())
                    .withRecordsFiltered(data.getRecordsFiltered())
                    .withRecordsTotal(data.getRecordsTotal())
                    .build();
        } catch (IOException e) {
            LOG.error("Error getting news items from graph service", e);
            return SspResponse.builder(News.class)
                    .withError(List.of(e.getMessage()))
                    .build();
        }
    }
}
