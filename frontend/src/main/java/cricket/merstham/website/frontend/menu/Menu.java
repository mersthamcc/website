package cricket.merstham.website.frontend.menu;

import cricket.merstham.website.frontend.configuration.ViewConfiguration;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.inject.Provider;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Menu {
    private final String name;
    private final Map<String, String> arguments;
    private final URI destinationUrl;
    private final List<String> roles;
    private final Provider<List<Menu>> children;
    private final String icons;
    private final String displayName;
    private final boolean externalLink;

    public Menu(
            String name,
            Map<String, String> arguments,
            URI destinationUrl,
            List<String> roles,
            Provider<List<Menu>> children,
            String icons,
            String displayName,
            boolean externalLink) {
        this.name = name;
        this.arguments = arguments;
        this.destinationUrl = destinationUrl;
        this.roles = roles;
        this.children = children;
        this.icons = icons;
        this.displayName = displayName;
        this.externalLink = externalLink;
    }

    public Menu(
            String name,
            Map<String, String> arguments,
            URI destinationUrl,
            List<String> roles,
            Provider<List<Menu>> children,
            String icons) {
        this(name, arguments, destinationUrl, roles, children, icons, null, false);
    }

    public Menu(
            String name,
            Map<String, String> arguments,
            URI destinationUrl,
            List<String> roles,
            Provider<List<Menu>> children) {
        this(name, arguments, destinationUrl, roles, children, null);
    }

    public Menu(
            String name,
            Map<String, String> arguments,
            URI destinationUrl,
            List<String> roles,
            Provider<List<Menu>> children,
            boolean externalLink) {
        this(name, arguments, destinationUrl, roles, children, null, null, externalLink);
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getArguments() {
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
                var i = 0;
                for (var entry : arguments.entrySet()) {
                    builder.arg(i, entry.getValue());
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
        if (isNull(children)) return null;
        return children.get();
    }

    public boolean onActivePath(ViewConfiguration.CurrentRoute currentRoute) {
        if (isActiveNode(currentRoute)) return true;
        if (children != null) {
            for (var child : getChildren()) {
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
            for (var child : getChildren()) {
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

    public String getDisplayName() {
        if (nonNull(displayName)) return displayName;
        return name;
    }

    public boolean isExternalLink() {
        return externalLink;
    }
}
