package cricket.merstham.website.frontend.security.filters;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CognitoAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private static final Logger LOG =
            LoggerFactory.getLogger(CognitoAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException {
        LOG.info(
                "Handling authentication failure exception of type: {}",
                exception.getClass().getSimpleName());

        if (exception instanceof BadCredentialsException) {
            response.sendRedirect("/login?error=bad_credentials");
        }
    }
}
