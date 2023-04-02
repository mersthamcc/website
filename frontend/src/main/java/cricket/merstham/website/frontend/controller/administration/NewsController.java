package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.shared.dto.News;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.model.DataTableColumn;
import cricket.merstham.website.frontend.model.datatables.SspRequest;
import cricket.merstham.website.frontend.model.datatables.SspResponse;
import cricket.merstham.website.frontend.model.datatables.SspResponseDataWrapper;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
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
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_NEWS_AJAX_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_NEWS_BASE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_NEWS_DELETE_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_NEWS_EDIT_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_NEWS_NEW_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_NEWS_SAVE_ROUTE;
import static java.util.Objects.isNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller("AdminNewsController")
public class NewsController extends SspController<News> {
    private static final Logger LOG = LoggerFactory.getLogger(NewsController.class);
    public static final String ADMINISTRATION_NEWS_EDIT = "administration/news/edit";
    public static final String ADMINISTRATION_NEWS_LIST = "administration/news/list";
    public static final String HAS_ROLE_ROLE_NEWS = "hasRole('ROLE_NEWS')";
    public static final String NEWS = "news";
    public static final String ERRORS = "errors";

    private final NewsService newsService;
    private final OAuth2AuthorizedClientService clientService;

    @Autowired
    public NewsController(NewsService newsService, OAuth2AuthorizedClientService clientService) {
        this.newsService = newsService;
        this.clientService = clientService;
    }

    @GetMapping(value = ADMIN_NEWS_BASE, name = "admin-news-list")
    @PreAuthorize(HAS_ROLE_ROLE_NEWS)
    public ModelAndView list() {
        return new ModelAndView(
                ADMINISTRATION_NEWS_LIST,
                Map.of(
                        "newsColumns",
                        List.of(
                                new DataTableColumn().setKey("news.title").setFieldName("title"),
                                new DataTableColumn()
                                        .setKey("news.publishDate")
                                        .setFieldName("formattedPublishDate"))));
    }

    @GetMapping(value = ADMIN_NEWS_NEW_ROUTE, name = "admin-news-new")
    @PreAuthorize(HAS_ROLE_ROLE_NEWS)
    public ModelAndView newPost(
            HttpServletRequest request, CognitoAuthentication cognitoAuthentication) {
        var flash = RequestContextUtils.getInputFlashMap(request);
        if (isNull(flash) || flash.isEmpty()) {
            var now = Instant.now();
            var news =
                    News.builder()
                            .author(cognitoAuthentication.getOidcUser().getName())
                            .createdDate(now)
                            .publishDate(now)
                            .draft(false)
                            .uuid(UUID.randomUUID().toString())
                            .build();
            return new ModelAndView(ADMINISTRATION_NEWS_EDIT, Map.of(NEWS, news));
        } else {
            return new ModelAndView(
                    ADMINISTRATION_NEWS_EDIT,
                    Map.of(
                            NEWS, flash.get(NEWS),
                            ERRORS, flash.get(ERRORS)));
        }
    }

    @GetMapping(value = ADMIN_NEWS_EDIT_ROUTE, name = "admin-news-edit")
    @PreAuthorize(HAS_ROLE_ROLE_NEWS)
    public ModelAndView editPost(
            CognitoAuthentication cognitoAuthentication, @PathVariable("id") int id)
            throws IOException {
        News news = newsService.get(cognitoAuthentication.getOAuth2AccessToken(), id);
        return new ModelAndView(ADMINISTRATION_NEWS_EDIT, Map.of(NEWS, news));
    }

    @GetMapping(value = ADMIN_NEWS_DELETE_ROUTE, name = "admin-news-delete")
    @PreAuthorize(HAS_ROLE_ROLE_NEWS)
    public RedirectView deletePost(
            CognitoAuthentication cognitoAuthentication, @PathVariable("id") int id)
            throws IOException {
        newsService.delete(cognitoAuthentication.getOAuth2AccessToken(), id);
        return new RedirectView(ADMIN_NEWS_BASE);
    }

    @PostMapping(value = ADMIN_NEWS_SAVE_ROUTE, name = "admin-news-save")
    @PreAuthorize(HAS_ROLE_ROLE_NEWS)
    public RedirectView save(
            CognitoAuthentication cognitoAuthentication,
            News news,
            RedirectAttributes redirectAttributes)
            throws IOException {
        try {
            newsService.saveNewsItem(cognitoAuthentication.getOAuth2AccessToken(), news);
            return new RedirectView(ADMIN_NEWS_BASE);
        } catch (EntitySaveException ex) {
            redirectAttributes.addFlashAttribute(ERRORS, ex.getErrors());
            redirectAttributes.addFlashAttribute(NEWS, news);
            return new RedirectView(ADMIN_NEWS_NEW_ROUTE);
        }
    }

    @Override
    @PostMapping(
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE,
            path = ADMIN_NEWS_AJAX_ROUTE)
    public @ResponseBody SspResponse<SspResponseDataWrapper<News>> getData(
            CognitoAuthentication cognitoAuthentication, @RequestBody SspRequest request) {
        try {
            var data =
                    newsService.getItems(
                            cognitoAuthentication.getOAuth2AccessToken(),
                            request.getStart(),
                            request.getLength(),
                            request.getSearch().getValue());
            return SspResponse.<SspResponseDataWrapper<News>>builder()
                    .draw(request.getDraw())
                    .data(data.getData())
                    .recordsFiltered(data.getRecordsFiltered())
                    .recordsTotal(data.getRecordsTotal())
                    .build();
        } catch (IOException e) {
            LOG.error("Error getting news items from graph service", e);
            return SspResponse.<SspResponseDataWrapper<News>>builder()
                    .error(Optional.of(List.of(e.getMessage())))
                    .build();
        }
    }
}
