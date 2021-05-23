package cricket.merstham.website.frontend.menu;

import cricket.merstham.website.frontend.configuration.ViewConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Singleton;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@Singleton
public class MenuBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(MenuBuilder.class);

    private final List<Menu> topMenu =
            List.of(
                    new Menu("register", null, null, List.of(), null),
                    new Menu(
                            "administration",
                            null,
                            URI.create("/administration"),
                            List.of("ROLE_ADMIN"),
                            null),
                    new Menu("help", null, URI.create("/help"), List.of(), null));

    private final List<Menu> userMenu = List.of(new Menu("logout", null, null, List.of(), null));

    private final List<Menu> frontEndMenu =
            List.of(
                    new Menu("home", null, null, List.of(), null),
                    new Menu("news", null, URI.create("/news"), List.of(), null),
                    new Menu(
                            "cricket",
                            null,
                            URI.create("#"),
                            List.of(),
                            List.of(
                                    new Menu("selection", null, null, List.of(), null),
                                    new Menu("fixtures", null, null, List.of(), null),
                                    new Menu("results", null, null, List.of(), null),
                                    new Menu(
                                            "result-archive",
                                            null,
                                            URI.create("javascript:;"),
                                            List.of(),
                                            List.of(
                                                    new Menu(
                                                            "results-for-year",
                                                            buildParams("year", "2020"),
                                                            null,
                                                            List.of(),
                                                            null),
                                                    new Menu(
                                                            "results-for-year",
                                                            buildParams("year", "2019"),
                                                            null,
                                                            List.of(),
                                                            null))))),
                    new Menu("social", null, URI.create("/social"), List.of(), null));

    public List<Menu> getTopMenu() {
        return topMenu;
    }

    public List<Menu> getUserMenu() {
        return userMenu;
    }

    public List<Menu> getFrontEndMenu() {
        return frontEndMenu;
    }

    public LinkedHashMap<String, String> buildParams(String... params) {
        if (params.length % 2 != 0)
            throw new IllegalArgumentException("Number of parameters should be even");
        var map = new LinkedHashMap<String, String>();

        for (int i = 0; i < params.length; i = i + 2) {
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
}
