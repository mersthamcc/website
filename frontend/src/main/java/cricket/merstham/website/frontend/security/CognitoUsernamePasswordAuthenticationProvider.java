package cricket.merstham.website.frontend.security;

import cricket.merstham.website.frontend.service.CognitoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class CognitoUsernamePasswordAuthenticationProvider implements AuthenticationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(CognitoService.class);

    private final CognitoService cognitoService;

    @Autowired
    public CognitoUsernamePasswordAuthenticationProvider(CognitoService cognitoService) {
        this.cognitoService = cognitoService;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return cognitoService.login(
                    authentication.getName(), authentication.getCredentials().toString());
        } else if (authentication instanceof CognitoAuthentication) {
            return cognitoService.refresh((CognitoAuthentication) authentication);
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(CognitoAuthentication.class)
                || authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
