package cricket.merstham.website.frontend.helpers;

import java.security.Principal;

import static java.util.Objects.isNull;

public class RoleHelper {
    public static String ADMIN = "ROLE_ADMIN";
    public static String NEWS = "ROLE_NEWS";
    public static String MEMBERSHIP = "ROLE_MEMBERSHIP";

    public static boolean hasRole(Principal principal, final String role) {
        if (isNull(principal)) return false;
        return true; // ((KeycloakAuthenticationToken) principal)
//                .getAuthorities().stream().anyMatch(r -> r.getAuthority().equals(role));
    }
}
