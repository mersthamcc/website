package cricket.merstham.website.frontend.security.providers;

import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.CognitoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class CognitoRefreshTokenAuthenticationProvider implements AuthenticationProvider {
    private static final Logger LOG =
            LoggerFactory.getLogger(CognitoRefreshTokenAuthenticationProvider.class);

    private final CognitoService cognitoService;

    @Autowired
    public CognitoRefreshTokenAuthenticationProvider(CognitoService cognitoService) {
        this.cognitoService = cognitoService;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        return cognitoService.refresh((CognitoAuthentication) authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(CognitoAuthentication.class);
    }
}
