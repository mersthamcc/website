package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.shared.dto.StaticPage;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.menu.MenuBuilder;
import cricket.merstham.website.frontend.model.DataTableColumn;
import cricket.merstham.website.frontend.model.datatables.SspRequest;
import cricket.merstham.website.frontend.model.datatables.SspResponse;
import cricket.merstham.website.frontend.model.datatables.SspResponseDataWrapper;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.PageService;
import jakarta.servlet.http.HttpServletRequest;
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

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_PAGE_AJAX_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_PAGE_BASE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_PAGE_DELETE_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_PAGE_EDIT_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_PAGE_NEW_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_PAGE_SAVE_ROUTE;
import static java.util.Objects.isNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller("adminPageController")
public class PageController extends SspController<StaticPage> {
    private static final Logger LOG = LoggerFactory.getLogger(PageController.class);
    public static final String ADMINISTRATION_PAGE_EDIT = "administration/page/edit";
    public static final String ADMINISTRATION_PAGE_LIST = "administration/page/list";
    public static final String HAS_ROLE_ROLE_PAGES = "hasRole('ROLE_PAGES')";
    public static final String PAGE = "page";
    public static final String ERRORS = "errors";
    public static final String MENUS = "menus";

    private final PageService service;
    private final MenuBuilder menuBuilder;

    @Autowired
    public PageController(PageService service, MenuBuilder menuBuilder) {
        this.service = service;
        this.menuBuilder = menuBuilder;
    }

    @GetMapping(value = ADMIN_PAGE_BASE, name = "admin-page-list")
    @PreAuthorize(HAS_ROLE_ROLE_PAGES)
    public ModelAndView list() {
        return new ModelAndView(
                ADMINISTRATION_PAGE_LIST,
                Map.of(
                        "columns",
                        List.of(
                                new DataTableColumn().setKey("page.slug").setFieldName("slug"),
                                new DataTableColumn().setKey("page.title").setFieldName("title"))));
    }

    @GetMapping(value = ADMIN_PAGE_NEW_ROUTE, name = "admin-page-new")
    @PreAuthorize(HAS_ROLE_ROLE_PAGES)
    public ModelAndView create(HttpServletRequest request) {
        var flash = RequestContextUtils.getInputFlashMap(request);
        if (isNull(flash) || flash.isEmpty()) {
            var page = StaticPage.builder().build();
            return new ModelAndView(
                    ADMINISTRATION_PAGE_EDIT, Map.of(PAGE, page, MENUS, getMenus()));
        } else {
            StaticPage page = (StaticPage) flash.get(PAGE);
            return new ModelAndView(
                    ADMINISTRATION_PAGE_EDIT,
                    Map.of(PAGE, page, ERRORS, flash.get(ERRORS), MENUS, getMenus()));
        }
    }

    @GetMapping(value = ADMIN_PAGE_EDIT_ROUTE, name = "admin-page-edit")
    @PreAuthorize(HAS_ROLE_ROLE_PAGES)
    public ModelAndView edit(
            CognitoAuthentication cognitoAuthentication, @PathVariable("slug") String slug)
            throws IOException {
        var contact = service.get(cognitoAuthentication.getOAuth2AccessToken(), slug);
        return new ModelAndView(ADMINISTRATION_PAGE_EDIT, Map.of(PAGE, contact, MENUS, getMenus()));
    }

    @GetMapping(value = ADMIN_PAGE_DELETE_ROUTE, name = "admin-page-delete")
    @PreAuthorize(HAS_ROLE_ROLE_PAGES)
    public RedirectView delete(
            CognitoAuthentication cognitoAuthentication, @PathVariable("slug") String slug)
            throws IOException {
        service.delete(cognitoAuthentication.getOAuth2AccessToken(), slug);
        return redirectTo(ADMIN_PAGE_BASE);
    }

    @PostMapping(value = ADMIN_PAGE_SAVE_ROUTE, name = "admin-page-save")
    @PreAuthorize(HAS_ROLE_ROLE_PAGES)
    public RedirectView save(
            CognitoAuthentication cognitoAuthentication,
            StaticPage page,
            RedirectAttributes redirectAttributes)
            throws IOException {
        try {
            service.saveItem(cognitoAuthentication.getOAuth2AccessToken(), page);
            return redirectTo(ADMIN_PAGE_BASE);
        } catch (EntitySaveException ex) {
            redirectAttributes.addFlashAttribute(ERRORS, ex.getErrors());
            redirectAttributes.addFlashAttribute(PAGE, page);
            return redirectTo(ADMIN_PAGE_NEW_ROUTE);
        }
    }

    @Override
    @PostMapping(
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE,
            path = ADMIN_PAGE_AJAX_ROUTE)
    public @ResponseBody SspResponse<SspResponseDataWrapper<StaticPage>> getData(
            CognitoAuthentication cognitoAuthentication, @RequestBody SspRequest request) {
        try {
            var data =
                    service.getItems(
                            cognitoAuthentication.getOAuth2AccessToken(),
                            request.getStart(),
                            request.getLength(),
                            request.getSearch().getValue());
            return SspResponse.<SspResponseDataWrapper<StaticPage>>builder()
                    .draw(request.getDraw())
                    .data(data.getData())
                    .recordsFiltered(data.getRecordsFiltered())
                    .recordsTotal(data.getRecordsTotal())
                    .build();
        } catch (IOException e) {
            LOG.error("Error getting contact items from graph service", e);
            return SspResponse.<SspResponseDataWrapper<StaticPage>>builder()
                    .error(Optional.of(List.of(e.getMessage())))
                    .build();
        }
    }

    private Map<String, String> getMenus() {
        var menus = new LinkedHashMap<String, String>();
        menus.put("", "menu.none");
        menuBuilder.getFrontEndMenu().stream()
                .filter(m -> m.getChildren() != null)
                .forEach(menu -> menus.put(menu.getName(), "menu." + menu.getDisplayName()));
        return menus;
    }
}
