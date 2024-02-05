package cricket.merstham.website.frontend.security.filters;

import cricket.merstham.website.frontend.security.SealedString;
import cricket.merstham.website.frontend.security.exceptions.CognitoUserNotVerifiedException;
import cricket.merstham.website.frontend.service.CognitoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static cricket.merstham.website.frontend.controller.LoginController.EMAIL;
import static cricket.merstham.website.frontend.controller.LoginController.PENDING_PASSWORD;
import static cricket.merstham.website.frontend.controller.LoginController.PENDING_USER;
import static cricket.merstham.website.frontend.controller.LoginController.VERIFICATION_URL;

@Component
public class CognitoAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private static final Logger LOG =
            LoggerFactory.getLogger(CognitoAuthenticationFailureHandler.class);

    private final String salt;
    private final CognitoService cognitoService;

    @Autowired
    public CognitoAuthenticationFailureHandler(
            @Value("${spring.security.oauth2.client.registration.login.session-salt:#{null}}")
                    String salt,
            CognitoService cognitoService) {
        this.salt = salt;
        this.cognitoService = cognitoService;
    }

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

        if (exception instanceof CognitoUserNotVerifiedException) {
            String username = request.getParameter("email");
            String password = request.getParameter("password");

            var pendingUser = cognitoService.resendVerificationCode(username);
            request.getSession().setAttribute(PENDING_USER, pendingUser);
            request.getSession().setAttribute(EMAIL, username);
            request.getSession()
                    .setAttribute(
                            PENDING_PASSWORD,
                            new SealedString(password, pendingUser.getUserId(), salt));
            response.sendRedirect(VERIFICATION_URL);
        }
    }
}
