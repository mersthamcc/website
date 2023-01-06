package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.service.CognitoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Objects;

@Controller
public class LoginController {

    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);
    public static final String LOGIN_PROCESSING_URL = "/login_processor";
    public static final String LOGIN_URL = "/login";
    public static final String SIGNUP_URL = "/sign-up";
    public static final String VERIFICATION_URL = "/sign-up/verification";
    public static final String PENDING_USER = "pending_user";
    private final CognitoService cognitoService;

    @Autowired
    public LoginController(CognitoService cognitoService) {
        this.cognitoService = cognitoService;
    }

    @GetMapping(value = LOGIN_URL, name = "login")
    public ModelAndView login() {
        return new ModelAndView("login/login", Map.of("loginProcessingUrl", LOGIN_PROCESSING_URL));
    }

    @GetMapping(value = SIGNUP_URL, name = "signup")
    public ModelAndView signup() {
        return new ModelAndView("login/signup");
    }

    @PostMapping(value = SIGNUP_URL, name = "signup_process")
    public RedirectView signupProcess(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("familyName") String familyName,
            @RequestParam("givenName") String givenName,
            RedirectAttributes redirectAttributes) {
        if (!Objects.equals(password, confirmPassword)) {
            redirectAttributes.addFlashAttribute("ERROR", "Passwords do not match");
            return new RedirectView(SIGNUP_URL);
        }
        var pendingUser = cognitoService.register(email, password, givenName, familyName);
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
            @RequestParam("userId") String userId, @RequestParam("code") String code) {
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
