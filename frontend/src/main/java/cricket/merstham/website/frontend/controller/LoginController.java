package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.model.UserSignUp;
import cricket.merstham.website.frontend.security.CognitoChallengeAuthentication;
import cricket.merstham.website.frontend.service.CognitoService;
import cricket.merstham.website.frontend.service.QrCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

import static cricket.merstham.website.frontend.helpers.OtpHelper.OTP_CODE_FIELD_PREFIX;
import static cricket.merstham.website.frontend.helpers.OtpHelper.getCodeFromMap;
import static java.text.MessageFormat.format;
import static java.util.Objects.nonNull;

@Controller
public class LoginController {

    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
    public static final String LOGIN_PROCESSING_URL = "/login_processor";
    public static final String CHALLENGE_PROCESSING_URL = "/login/challenge";
    public static final String LOGIN_URL = "/login";
    public static final String SIGNUP_URL = "/sign-up";
    public static final String VERIFICATION_URL = "/sign-up/verification";
    public static final String PENDING_USER = "pending_user";
    public static final String ERROR_FLASH = "ERROR";
    public static final String ERRORS = "ERRORS";
    public static final String SIGN_UP = "VALUES";
    public static final String SETUP_MFA_APP_URL = "/auth/challenge/setup-mfa/app";
    public static final String SETUP_MFA_SMS_URL = "/auth/challenge/setup-mfa/sms";
    private final CognitoService cognitoService;
    private final QrCodeService qrCodeService;
    @Autowired
    public LoginController(CognitoService cognitoService, QrCodeService qrCodeService) {
        this.cognitoService = cognitoService;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping(value = LOGIN_URL, name = "login")
    public ModelAndView login() {
        return new ModelAndView("login/login", Map.of("processingUrl", LOGIN_PROCESSING_URL));
    }

    @GetMapping(value = "/auth/challenge/{type}", name = "challenge")
    @PreAuthorize("hasRole('ROLE_PRE_AUTH')")
    public ModelAndView challenge(
            @PathVariable String type,
            CognitoChallengeAuthentication authentication) {
        return new ModelAndView(
                format("login/challenge-{0}", type),
                Map.of(
                        "processingUrl", CHALLENGE_PROCESSING_URL,
                        "sessionId", authentication.getSessionId(),
                        "userId", authentication.getPrincipal(),
                        "authentication", authentication,
                        "challengeName", authentication.getChallengeName().toString()));
    }

    @GetMapping(value = SETUP_MFA_APP_URL, name = "setup-mfa-app")
    @PreAuthorize("hasRole('ROLE_PRE_AUTH')")
    public ModelAndView mfaAppSetup(
            CognitoChallengeAuthentication authentication) {

        var credentials = (Map<String, Object>) authentication.getCredentials();
        var qrCode = qrCodeService.getTotpSecretQrCode(
                (String) credentials.get("SOFTWARE_TOKEN_MFA_CODE"),
                authentication.getEmail());

        return new ModelAndView(
                "login/setup-mfa-app",
                Map.of(
                        "processingUrl", CHALLENGE_PROCESSING_URL,
                        "sessionId", authentication.getSessionId(),
                        "userId", authentication.getPrincipal(),
                        "authentication", authentication,
                        "qrCode", qrCode,
                        "secret", authentication.getCredentials()));
    }

    @GetMapping(value = SETUP_MFA_SMS_URL, name = "setup-mfa-sms")
    @PreAuthorize("hasRole('ROLE_PRE_AUTH')")
    public ModelAndView mfaSmsSetup(
            CognitoChallengeAuthentication authentication) {
        return new ModelAndView(
                "login/setup-mfa-sms",
                Map.of(
                        "processingUrl", CHALLENGE_PROCESSING_URL,
                        "sessionId", authentication.getSessionId(),
                        "userId", authentication.getPrincipal(),
                        "authentication", authentication));
    }
    @GetMapping(value = SIGNUP_URL, name = "signup")
    public ModelAndView signup(
            HttpServletRequest request) {
        var model = Map.<String, Object>of();
        var flash = RequestContextUtils.getInputFlashMap(request);
        if (nonNull(flash)) {
            var errors = flash.get(ERRORS);
            var signUp = flash.get(SIGN_UP);
            model = Map.of("errors", errors, "signUp", signUp);
        }
        return new ModelAndView("login/signup", model);
    }

    @PostMapping(value = SIGNUP_URL, name = "signup_process")
    public RedirectView signupProcess(
            @Valid UserSignUp signUp,
            Errors errors,
            RedirectAttributes redirectAttributes) {
        if (errors.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    ERRORS,
                    errors.getAllErrors());
            redirectAttributes.addFlashAttribute(SIGN_UP, signUp);

            return new RedirectView(SIGNUP_URL);
        }
        var pendingUser = cognitoService.register(signUp);
        redirectAttributes.addFlashAttribute(PENDING_USER, pendingUser);

        return new RedirectView(VERIFICATION_URL);
    }

    @GetMapping(value = VERIFICATION_URL, name = "verification")
    public ModelAndView verification(HttpServletRequest request) {
        var flash = RequestContextUtils.getInputFlashMap(request);
        var pendingUser = flash.get(PENDING_USER);
        return new ModelAndView("login/verification", Map.of("pendingUser", pendingUser));
    }

    @PostMapping(value = VERIFICATION_URL, name = "verification_process")
    public RedirectView verificationProcess(
            @RequestParam Map<String, String> parameters) {
        var userId = parameters.get("userId");
        var code = getCodeFromMap(parameters, OTP_CODE_FIELD_PREFIX);
        if (cognitoService.verify(userId, code)) {
            return new RedirectView("/");
        }
        return new RedirectView(SIGNUP_URL);
    }

    @GetMapping(value = "/logout", name = "logout")
    public RedirectView logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return new RedirectView("/");
    }
}
