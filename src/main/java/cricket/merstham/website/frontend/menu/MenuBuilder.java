package cricket.merstham.website.frontend.menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static java.text.MessageFormat.format;

@Service
public class MenuBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(MenuBuilder.class);

    public List<Menu> getTopMenu() {
        return List.of(
                new Menu("register", null, URI.create("/register"), List.of(), null, false),
                new Menu("administration", null, URI.create("/administration"), List.of("ROLE_ADMIN"), null, false),
                new Menu("help", null, URI.create("/help"), List.of(), null, false)
        );
    }

    public List<Menu> getUserMenu() {
        return List.of(
                new Menu("logout", null, URI.create("/logout"), List.of(), null, false)
        );
    }

    public List<Menu> getFrontEndMenu(String currentRouteName) {
        LOG.info(format("Current Route = {0}", currentRouteName));
        return List.of(
                new Menu("home",
                        null,
                        URI.create(MvcUriComponentsBuilder.fromMappingName("home").build()),
                        List.of(),
                        null,
                        currentRouteName.equals("home")
                ),
                new Menu("news",
                        null,
                        URI.create("/news"),
                        List.of(),
                        null,
                        currentRouteName == "news"
                ),
                new Menu("cricket",
                        null,
                        URI.create("#"),
                        List.of(),
                        null,
                        currentRouteName == "cricket"
                ),
                new Menu("social",
                        null,
                        URI.create("/social"),
                        List.of(),
                        null,
                        currentRouteName == "social"
                )
        );
    }
}

