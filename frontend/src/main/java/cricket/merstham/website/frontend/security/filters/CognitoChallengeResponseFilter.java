package cricket.merstham.website.frontend.security.filters;

import cricket.merstham.website.frontend.security.CognitoChallengeAuthentication;
import cricket.merstham.website.frontend.service.CognitoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static cricket.merstham.shared.helpers.InputSanitizer.encodeForLog;
import static cricket.merstham.website.frontend.controller.LoginController.CHALLENGE_PROCESSING_URL;
import static cricket.merstham.website.frontend.helpers.OtpHelper.getCodeFromRequestParameters;
import static cricket.merstham.website.frontend.security.CognitoChallengeAuthentication.Step.SETUP_SMS_MFA;

@Component
public class CognitoChallengeResponseFilter extends AbstractAuthenticationProcessingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(CognitoChallengeResponseFilter.class);
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher(CHALLENGE_PROCESSING_URL, "POST");
    public static final String MFA_TYPE_FIELD = "mfa-type";

    private final CognitoService service;

    public CognitoChallengeResponseFilter(CognitoService service) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.service = service;
    }

    @Autowired
    public CognitoChallengeResponseFilter(
            AuthenticationManager authenticationManager, CognitoService service) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.service = service;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        var authentication =
                (CognitoChallengeAuthentication)
                        SecurityContextHolder.getContext().getAuthentication();
        switch (authentication.getChallengeName()) {
            case MFA_SETUP -> {
                return mfaSetup(request, response, authentication);
            }
            case SOFTWARE_TOKEN_MFA -> {
                return verifySoftwareMfa(request, authentication);
            }
            case SMS_MFA -> {
                return verifySmsMfa(request, authentication);
            }
            case NEW_PASSWORD_REQUIRED -> {
                return handlePasswordChange(request, authentication);
            }
            case SELECT_MFA_TYPE -> {
                return chooseMfaType(request, response, authentication);
            }
            default -> {
                if (LOG.isErrorEnabled())
                    LOG.error(
                            "Unsupported Cognito challenge experienced: {}",
                            encodeForLog(authentication.getChallengeName().toString()));
                return null;
            }
        }
    }

    private Authentication handlePasswordChange(
            HttpServletRequest request, CognitoChallengeAuthentication authentication) {
        return null;
    }

    private Authentication verifySmsMfa(
            HttpServletRequest request, CognitoChallengeAuthentication authentication) {
        var code = getCodeFromRequestParameters(request.getParameterMap());
        return service.verifySmsMfa(authentication, code);
    }

    private Authentication mfaSetup(
            HttpServletRequest request,
            HttpServletResponse response,
            CognitoChallengeAuthentication authentication)
            throws IOException {
        switch (authentication.getStep()) {
            case DEFAULT -> {
                return chooseMfaTypeToSetup(request, response, authentication);
            }
            case SETUP_SOFTWARE_MFA -> {
                return service.verifyAppSetup(
                        authentication, getCodeFromRequestParameters(request.getParameterMap()));
            }
            case SETUP_SMS_MFA -> {
                return service.setPhoneNumber(authentication, request.getParameter("phoneNumber"));
            }
            default -> {
                LOG.error("Unexpected challenge step experienced: {}", authentication.getStep());
                return null;
            }
        }
    }

    private Authentication chooseMfaType(
            HttpServletRequest request,
            HttpServletResponse response,
            CognitoChallengeAuthentication authentication) {
        var mfaType = request.getParameter(MFA_TYPE_FIELD);
        return service.selectMfaType(authentication, mfaType);
    }

    private Authentication chooseMfaTypeToSetup(
            HttpServletRequest request,
            HttpServletResponse response,
            CognitoChallengeAuthentication authentication) {
        var mfaType = request.getParameter(MFA_TYPE_FIELD);
        switch (mfaType) {
            case "SOFTWARE_TOKEN_MFA" -> {
                return service.getAppToken(authentication);
            }
            case "SMS_MFA" -> {
                return authentication.toBuilder().step(SETUP_SMS_MFA).build();
            }
            default -> {
                LOG.error("Unexpected MFA type in request: {}", mfaType);
                return null;
            }
        }
    }

    private Authentication verifySoftwareMfa(
            HttpServletRequest request, CognitoChallengeAuthentication authentication) {
        var code = getCodeFromRequestParameters(request.getParameterMap());
        return service.verifySoftwareMfa(authentication, code);
    }
}
