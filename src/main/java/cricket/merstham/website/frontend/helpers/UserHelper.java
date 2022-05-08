package cricket.merstham.website.frontend.helpers;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.security.Principal;

public class UserHelper {
    public static String getUserFullName(Principal principal) {
        OidcUser user = (OidcUser) ((OAuth2AuthenticationToken) principal).getPrincipal();
        return user.getFullName();
    }
}
