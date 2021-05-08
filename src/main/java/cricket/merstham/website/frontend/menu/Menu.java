package cricket.merstham.website.frontend.menu;

import java.net.URI;
import java.util.List;

public class Menu {
    private String name;
    private String className;
    private URI destinationUrl;
    private List<String> roles;
    private List<Menu> children;
    private boolean active;

    public Menu(String name, String className, URI destinationUrl, List<String> roles, List<Menu> children, boolean active) {
        this.name = name;
        this.className = className;
        this.destinationUrl = destinationUrl;
        this.roles = roles;
        this.children = children;
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public URI getDestinationUrl() {
        return destinationUrl;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<Menu> getChildren() {
        return children;
    }

    public boolean isActive() {
        return active;
    }
}
