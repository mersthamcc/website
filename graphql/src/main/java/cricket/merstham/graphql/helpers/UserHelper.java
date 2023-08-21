package cricket.merstham.graphql.helpers;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.security.Principal;
import java.util.List;

public class UserHelper {
    public static String getSubject(Principal principal) {
        var user = ((JwtAuthenticationToken) principal);
        return user.getName();
    }

    public static String getEmail(Principal principal) {
        var user = ((JwtAuthenticationToken) principal);
        return user.getName();
    }

    public static List<String> getRoles(Principal principal) {
        var user = ((JwtAuthenticationToken) principal);
        return user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    }
}
