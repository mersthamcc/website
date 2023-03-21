package cricket.merstham.website.frontend.security.filters;

import cricket.merstham.website.frontend.security.CognitoChallengeAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static cricket.merstham.website.frontend.controller.LoginController.SETUP_MFA_APP_URL;
import static cricket.merstham.website.frontend.controller.LoginController.SETUP_MFA_SMS_URL;

@Component
public class CognitoChallengeFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(CognitoChallengeFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof CognitoChallengeAuthentication
                && !request.getRequestURI().startsWith("/auth/challenge/")
                && !request.getRequestURI().startsWith("/logout")) {
            var challenge = (CognitoChallengeAuthentication) authentication;
            switch (challenge.getChallengeName()) {
                case MFA_SETUP -> {
                    mfaSetup(request, response, challenge);
                    return;
                }
                case SELECT_MFA_TYPE -> {
                    response.sendRedirect("/auth/challenge/select-mfa");
                    return;
                }
                case SOFTWARE_TOKEN_MFA, SMS_MFA -> {
                    response.sendRedirect("/auth/challenge/mfa");
                    return;
                }
                case PASSWORD_VERIFIER -> {
                    response.sendRedirect("/auth/challenge/reset-password");
                    return;
                }
                default -> LOG.warn(
                        "Unsupported Cognito challenge experienced: {}, continuing with chain",
                        challenge.getChallengeName());
            }
        }
        filterChain.doFilter(request, response);
    }

    private void mfaSetup(
            HttpServletRequest request,
            HttpServletResponse response,
            CognitoChallengeAuthentication challenge)
            throws IOException {
        switch (challenge.getStep()) {
            case SETUP_SOFTWARE_MFA -> response.sendRedirect(SETUP_MFA_APP_URL);
            case SETUP_SMS_MFA -> response.sendRedirect(SETUP_MFA_SMS_URL);
            default -> response.sendRedirect("/auth/challenge/setup-mfa");
        }
    }
}
