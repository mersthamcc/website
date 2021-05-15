package cricket.merstham.website.frontend.menu;

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;
import java.util.List;

public class Menu {
    private String name;
    private String className;
    private URI destinationUrl;
    private List<String> roles;
    private List<Menu> children;

    public Menu(String name, String className, URI destinationUrl, List<String> roles, List<Menu> children) {
        this.name = name;
        this.className = className;
        this.destinationUrl = destinationUrl;
        this.roles = roles;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public URI getDestinationUrl() {
        if (destinationUrl==null) {
            return URI.create(MvcUriComponentsBuilder.fromMappingName(name).build());
        }
        return destinationUrl;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<Menu> getChildren() {
        return children;
    }

    public boolean onActivePath(String currentRoute) {
        if (currentRoute.equals(name)) return true;
        if (children != null ) {
            for (var child : children) {
                if (child.onActivePath(currentRoute)) return true;
            }
        }
        return false;
    }
}
