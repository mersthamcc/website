package cricket.merstham.website.frontend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/login", name = "login")
    public RedirectView login() {
        return new RedirectView("/");
    }

    @GetMapping(value = "/logout", name = "logout")
    public RedirectView logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return new RedirectView("/");
    }
}
