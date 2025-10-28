package cricket.merstham.website.frontend.helpers;

import cricket.merstham.website.frontend.security.CognitoAuthentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.security.Principal;

public class UserHelper {

    private UserHelper() {}

    public static OAuth2AccessToken getAccessToken(Principal principal) {
        if (principal instanceof CognitoAuthentication cognitoAuthentication) {
            return cognitoAuthentication.getOAuth2AccessToken();
        }
        throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass());
    }
}
