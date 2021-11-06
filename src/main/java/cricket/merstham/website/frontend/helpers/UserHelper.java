package cricket.merstham.website.frontend.helpers;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.IDToken;

import java.security.Principal;

public class UserHelper {
    public static String getUserFullName(Principal principal) {
        IDToken idToken = getIdToken(principal);
        return idToken.getName();
    }

    private static IDToken getIdToken(Principal principal) {
        return ((KeycloakPrincipal)((KeycloakAuthenticationToken) principal).getPrincipal()).getKeycloakSecurityContext().getIdToken();
    }
}
