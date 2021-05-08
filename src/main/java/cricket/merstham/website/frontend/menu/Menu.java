package cricket.merstham.website.frontend.menu;

import java.net.URI;
import java.util.List;

public record Menu(String name, String className, URI destinationUrl, List<String> roles, List<Menu> children, boolean active) {
}
