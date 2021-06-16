package cricket.merstham.website.frontend.menu;

import cricket.merstham.website.frontend.configuration.ViewConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Menu {
    private static final Logger LOG = LoggerFactory.getLogger(Menu.class);

    private final String name;
    private final LinkedHashMap<String, String> arguments;
    private final URI destinationUrl;
    private final List<String> roles;
    private final List<Menu> children;
    private final String icons;

    public Menu(
            String name,
            LinkedHashMap<String, String> arguments,
            URI destinationUrl,
            List<String> roles,
            List<Menu> children, String icons) {
        this.name = name;
        this.arguments = arguments;
        this.destinationUrl = destinationUrl;
        this.roles = roles;
        this.children = children;
        this.icons = icons;
    }

    public Menu(
            String name,
            LinkedHashMap<String, String> arguments,
            URI destinationUrl,
            List<String> roles,
            List<Menu> children) {
        this(name, arguments, destinationUrl, roles, children, null);
    }

    public String getName() {
        return name;
    }

    public LinkedHashMap<String, String> getArguments() {
        return arguments;
    }

    public String[] getArgumentValues() {
        if (arguments == null) return new String[] {};
        return arguments.values().toArray(new String[0]);
    }

    public URI getDestinationUrl() {
        if (destinationUrl == null) {
            MvcUriComponentsBuilder.MethodArgumentBuilder builder =
                    MvcUriComponentsBuilder.fromMappingName(name);
            if (arguments != null) {
                int i = 0;
                for (var key : arguments.keySet()) {
                    builder.arg(i, arguments.get(key));
                    i++;
                }
            }
            return URI.create(builder.build());
        }
        return destinationUrl;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<Menu> getChildren() {
        return children;
    }

    public boolean onActivePath(ViewConfiguration.CurrentRoute currentRoute) {
        LOG.debug(
                "onActivePath(name = {}, params = {}) called on name = {}, params = {}",
                currentRoute.getName(),
                currentRoute.getPathVariables(),
                this.name,
                this.getArguments());
        if (isActiveNode(currentRoute)) return true;
        if (children != null) {
            for (var child : children) {
                if (child.onActivePath(currentRoute)) return true;
            }
        }
        return false;
    }

    public boolean isActiveNode(ViewConfiguration.CurrentRoute currentRoute) {
        if (currentRoute.getName().equals(name)) {
            if ((currentRoute.getPathVariables() == null
                            || currentRoute.getPathVariables().isEmpty())
                    && (arguments == null || arguments.isEmpty())) {
                return true;
            }
            if (arguments == null) return false;
            return currentRoute.getPathVariables().equals(arguments);
        }
        return false;
    }

    public List<Menu> getBreadcrumbs(ViewConfiguration.CurrentRoute currentRoute) {
        if (isActiveNode(currentRoute)) return new ArrayList<>(List.of(this));
        if (children != null) {
            for (var child : children) {
                List<Menu> breadcrumbs = child.getBreadcrumbs(currentRoute);
                if (!breadcrumbs.isEmpty()) {
                    breadcrumbs.add(this);
                    return breadcrumbs;
                }
            }
        }
        return List.of();
    }

    public String getIcons() {
        return icons;
    }
}
