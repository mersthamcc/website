package cricket.merstham.website.frontend.menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.inject.Singleton;
import java.net.URI;
import java.util.List;

import static java.text.MessageFormat.format;

@Service
@Singleton
public class MenuBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(MenuBuilder.class);

    public List<Menu> getTopMenu() {
        return List.of(
                new Menu("register", null, null, List.of(), null),
                new Menu("administration", null, URI.create("/administration"), List.of("ROLE_ADMIN"), null),
                new Menu("help", null, URI.create("/help"), List.of(), null)
        );
    }

    public List<Menu> getUserMenu() {
        return List.of(
                new Menu("logout", null, null, List.of(), null)
        );
    }

    public List<Menu> getFrontEndMenu() {
        return List.of(
            new Menu("home",
                    null,
                    null,
                    List.of(),
                    null
            ),
            new Menu("news",
                    null,
                    URI.create("/news"),
                    List.of(),
                    null
            ),
            new Menu("cricket",
                    null,
                    URI.create("#"),
                    List.of(),
                    null
            ),
            new Menu("social",
                    null,
                    URI.create("/social"),
                    List.of(),
                    null
            )
        );
    }
}

