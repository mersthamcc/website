package cricket.merstham.website.frontend.helpers;

import cricket.merstham.website.frontend.security.CognitoAuthentication;

import java.security.Principal;

import static java.util.Objects.isNull;

public class RoleHelper {
    public static String ADMIN = "ROLE_ADMIN";
    public static String NEWS = "ROLE_NEWS";
    public static String EVENTS = "ROLE_EVENTS";
    public static String CONTACT = "ROLE_CONTACT";
    public static String MEMBERSHIP = "ROLE_MEMBERSHIP";

    public static boolean hasRole(Principal principal, final String role) {
        if (isNull(principal)) return false;
        return ((CognitoAuthentication) principal)
                .getAuthorities().stream().anyMatch(r -> r.getAuthority().equals(role));
    }
}
