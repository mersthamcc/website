package cricket.merstham.website.frontend.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class CognitoAuthenticationManager implements AuthenticationManager {

    //    private final List<AuthenticationProvider> providers;
    //
    //    public CognitoAuthenticationManager(List<AuthenticationProvider> providers) {
    //        this.providers = providers;
    //    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        return null;
    }
}
