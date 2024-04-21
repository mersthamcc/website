package cricket.merstham.website.frontend.menu;

import cricket.merstham.shared.dto.ContactCategory;
import cricket.merstham.website.frontend.configuration.ViewConfiguration;
import cricket.merstham.website.frontend.service.MenuService;
import cricket.merstham.website.frontend.service.PageService;
import jakarta.inject.Singleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import static cricket.merstham.website.frontend.helpers.RoutesHelper.NEWS_HOME_ROUTE;

@Service
@Singleton
public class MenuBuilder {

    private static final URI SCRIPT_LINK = URI.create("javascript:;");

    private final List<Menu> topMenu =
            List.of(
                    new Menu("register", null, null, List.of(), null),
                    new Menu(
                            "administration",
                            null,
                            URI.create("/administration"),
                            List.of("ROLE_ADMIN"),
                            null));
    //                    new Menu("help", null, URI.create("/help"), List.of(), null));

    private final List<Menu> userMenu =
            List.of(
                    new Menu("account", null, URI.create("/account"), List.of(), null),
                    new Menu("logout", null, null, List.of(), null));

    private final List<Menu> frontEndMenu =
            List.of(
                    new Menu("home", null, null, List.of(), null),
                    new Menu("news", null, URI.create(NEWS_HOME_ROUTE), List.of(), null),
                    new Menu(
                            "cricket",
                            null,
                            URI.create("#"),
                            List.of(),
                            () ->
                                    List.of(
                                            new Menu("fixtures", null, null, List.of(), null),
                                            new Menu(
                                                    "result-archive",
                                                    null,
                                                    SCRIPT_LINK,
                                                    List.of(),
                                                    this::getFixtureArchive))),
                    new Menu("events", null, URI.create("/events"), List.of(), null),
                    new Menu(
                            "about",
                            null,
                            URI.create("#"),
                            List.of(),
                            () ->
                                    List.of(
                                            new Menu(
                                                    "contacts",
                                                    null,
                                                    URI.create("#"),
                                                    List.of(),
                                                    this::getContactCategories))),
                    new Menu("venue", null, URI.create("#"), List.of(), this::getVenueList));

    private final List<Menu> dashboardMenu =
            List.of(
                    new Menu(
                            "admin-dashboards-top",
                            null,
                            SCRIPT_LINK,
                            List.of(),
                            () ->
                                    List.of(
                                            new Menu(
                                                    "admin-home",
                                                    null,
                                                    SCRIPT_LINK,
                                                    List.of(),
                                                    null),
                                            new Menu(
                                                    "admin-membership-dashboard",
                                                    null,
                                                    SCRIPT_LINK,
                                                    List.of(),
                                                    null)),
                            "tio-dashboard-outlined"));

    private final List<Menu> adminContentMenu =
            List.of(
                    new Menu(
                            "admin-news-top",
                            null,
                            SCRIPT_LINK,
                            List.of("ROLE_NEWS"),
                            () ->
                                    List.of(
                                            new Menu(
                                                    "admin-news-list", null, null, List.of(), null),
                                            new Menu(
                                                    "admin-news-new", null, null, List.of(), null)),
                            "tio-feed-outlined"),
                    new Menu(
                            "admin-event-top",
                            null,
                            SCRIPT_LINK,
                            List.of("ROLE_EVENTS"),
                            () ->
                                    List.of(
                                            new Menu(
                                                    "admin-event-list",
                                                    null,
                                                    null,
                                                    List.of(),
                                                    null),
                                            new Menu(
                                                    "admin-event-new",
                                                    null,
                                                    null,
                                                    List.of(),
                                                    null)),
                            "tio-calendar"),
                    new Menu(
                            "admin-contact-top",
                            null,
                            SCRIPT_LINK,
                            List.of("ROLE_CONTACT"),
                            () ->
                                    List.of(
                                            new Menu(
                                                    "admin-contact-list",
                                                    null,
                                                    null,
                                                    List.of(),
                                                    null),
                                            new Menu(
                                                    "admin-contact-new",
                                                    null,
                                                    null,
                                                    List.of(),
                                                    null)),
                            "tio-call"),
                    new Menu(
                            "admin-page-top",
                            null,
                            SCRIPT_LINK,
                            List.of("ROLE_PAGES"),
                            () ->
                                    List.of(
                                            new Menu(
                                                    "admin-page-list", null, null, List.of(), null),
                                            new Menu(
                                                    "admin-page-new", null, null, List.of(), null)),
                            "tio-pages-outlined"),
                    new Menu(
                            "admin-venue-top",
                            null,
                            SCRIPT_LINK,
                            List.of("ROLE_VENUES"),
                            () ->
                                    List.of(
                                            new Menu(
                                                    "admin-venue-list",
                                                    null,
                                                    null,
                                                    List.of(),
                                                    null),
                                            new Menu(
                                                    "admin-venue-new",
                                                    null,
                                                    null,
                                                    List.of(),
                                                    null)),
                            "tio-explore-outlined"));

    private final List<Menu> adminAdministrationMenu =
            List.of(
                    new Menu(
                            "admin-membership-top",
                            null,
                            SCRIPT_LINK,
                            List.of("ROLE_MEMBERSHIP"),
                            () ->
                                    List.of(
                                            new Menu(
                                                    "admin-membership-list",
                                                    null,
                                                    null,
                                                    List.of(),
                                                    null)),
                            "tio-group-senior"),
                    new Menu(
                            "admin-membership-reports",
                            null,
                            SCRIPT_LINK,
                            List.of("ROLE_MEMBERSHIP"),
                            () ->
                                    List.of(
                                            new Menu(
                                                    "admin-membership-report",
                                                    buildParams("report", "unpaid-members-report"),
                                                    null,
                                                    List.of(),
                                                    null),
                                            new Menu(
                                                    "admin-membership-report",
                                                    buildParams("report", "openage-junior-report"),
                                                    null,
                                                    List.of(),
                                                    null)),
                            "tio-chart-bar-4"));

    private final List<Menu> adminSystemMenu =
            List.of(
                    new Menu(
                            "admin-configuration-top",
                            null,
                            SCRIPT_LINK,
                            List.of("ROLE_SYSTEM"),
                            () ->
                                    List.of(
                                            new Menu(
                                                    "admin-configuration-list",
                                                    null,
                                                    SCRIPT_LINK,
                                                    List.of(),
                                                    null)),
                            "tio-tune"));

    private final List<Menu> accountMenu =
            List.of(
                    new Menu(
                            "account-home-top",
                            null,
                            SCRIPT_LINK,
                            List.of(),
                            () ->
                                    List.of(
                                            new Menu(
                                                    "account-home",
                                                    null,
                                                    null,
                                                    List.of(),
                                                    null,
                                                    "fa-id-card"),
                                            new Menu(
                                                    "account-home-security",
                                                    null,
                                                    null,
                                                    List.of(),
                                                    null,
                                                    "fa-shield-alt"))),
                    new Menu(
                            "account-members-top",
                            null,
                            SCRIPT_LINK,
                            List.of(),
                            () ->
                                    List.of(
                                            new Menu(
                                                    "account-members",
                                                    null,
                                                    null,
                                                    List.of(),
                                                    null,
                                                    "fa-users"))));
    //                                            new Menu(
    //                                                    "account-members-billing",
    //                                                    null,
    //                                                    null,
    //                                                    List.of(),
    //                                                    null,
    //                                                    "fa-credit-card"))));
    private final MenuService menuService;
    private final PageService pageService;

    @Autowired
    public MenuBuilder(MenuService menuService, PageService pageService) {
        this.menuService = menuService;
        this.pageService = pageService;
    }

    private List<Menu> getVenueList() {
        return menuService.getDynamicMenuItems().getVenues().stream()
                .map(
                        v ->
                                new Menu(
                                        "venue-item",
                                        buildParams("slug", v.getSlug()),
                                        null,
                                        List.of(),
                                        null,
                                        null,
                                        v.getName()))
                .toList();
    }

    private List<Menu> getFixtureArchive() {
        return menuService.getDynamicMenuItems().getSeasons().stream()
                .map(
                        s ->
                                new Menu(
                                        "fixtures-for-season",
                                        buildParams("year", s.toString()),
                                        null,
                                        List.of(),
                                        null))
                .toList();
    }

    private List<Menu> getContactCategories() {
        return menuService.getDynamicMenuItems().getContactCategories().stream()
                .sorted(Comparator.comparing(ContactCategory::getSortKey))
                .map(
                        s ->
                                new Menu(
                                        "contact-category",
                                        buildParams("category", s.getSlug()),
                                        null,
                                        List.of(),
                                        null,
                                        null,
                                        s.getTitle()))
                .toList();
    }

    public List<Menu> getTopMenu() {
        return topMenu;
    }

    public List<Menu> getUserMenu() {
        return userMenu;
    }

    public List<Menu> getFrontEndMenu() {
        return frontEndMenu;
    }

    public List<Menu> getAccountMenu() {
        return accountMenu;
    }

    public LinkedHashMap<String, String> buildParams(String... params) {
        if (params.length % 2 != 0)
            throw new IllegalArgumentException("Number of parameters should be even");
        var map = new LinkedHashMap<String, String>();

        for (var i = 0; i < params.length; i = i + 2) {
            map.put(params[i], params[i + 1]);
        }
        return map;
    }

    public List<Menu> getBreadcrumbs(ViewConfiguration.CurrentRoute currentRoute) {
        for (var item : frontEndMenu) {
            if (item.onActivePath(currentRoute)) {
                List<Menu> crumbs = item.getBreadcrumbs(currentRoute);
                Collections.reverse(crumbs);
                return crumbs;
            }
        }
        return List.of();
    }

    public List<Menu> getDashboardMenu() {
        return dashboardMenu;
    }

    public List<Menu> getAdminContentMenu() {
        return adminContentMenu;
    }

    public List<Menu> getAdminAdministrationMenu() {
        return adminAdministrationMenu;
    }

    public List<Menu> getAdminSystemMenu() {
        return adminSystemMenu;
    }
}
