package cricket.merstham.website.frontend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/login", name = "login", method = RequestMethod.GET)
    public RedirectView login() throws Exception {
        return new RedirectView("/");
    }

    @RequestMapping(value = "/logout", name = "logout", method = RequestMethod.GET)
    public RedirectView logout(HttpServletRequest request, SessionStatus status) throws Exception {
        status.setComplete();
        request.logout();
        return new RedirectView("/");
    }
}
