package cricket.merstham.website.frontend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("isAuthenticated()")
public class AccountController {

    @GetMapping(value = "/account", name = "account-home")
    public ModelAndView home() {
        return new ModelAndView("account/home");
    }
}
