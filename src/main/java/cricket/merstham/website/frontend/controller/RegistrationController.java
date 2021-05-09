package cricket.merstham.website.frontend.controller;

import org.keycloak.representations.AccessToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class RegistrationController {

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/register", name = "register", method = RequestMethod.GET)
    public ModelAndView register(AccessToken accessToken) {
        return new ModelAndView("registration/register");
    }
}
