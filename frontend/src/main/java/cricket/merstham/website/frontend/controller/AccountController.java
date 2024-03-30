package cricket.merstham.website.frontend.controller;

import cricket.merstham.shared.dto.User;
import cricket.merstham.website.frontend.service.CognitoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;

import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;
import static java.util.Objects.nonNull;

@Controller
@PreAuthorize("isAuthenticated()")
public class AccountController {

    public static final String ERRORS = "ERRORS";

    private final CognitoService service;

    @Autowired
    public AccountController(CognitoService service) {
        this.service = service;
    }

    @GetMapping(value = "/account", name = "account-home")
    public ModelAndView home(HttpServletRequest request) {
        var model = new HashMap<String, Object>();
        model.put("userDetails", service.getUserDetails());

        var statusCode = HttpStatus.OK;
        var flash = RequestContextUtils.getInputFlashMap(request);
        if (nonNull(flash) && flash.containsKey(ERRORS)) {
            model.put("errors", flash.get(ERRORS));
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ModelAndView("account/home", model, statusCode);
    }

    @PostMapping(value = "/account", name = "account-home-update-user")
    public RedirectView updateUser(User user, RedirectAttributes redirectAttributes) {
        var errors = service.updateUser(user);
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute(ERRORS, errors);
        }
        return redirectTo("/account");
    }
}
