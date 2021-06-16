package cricket.merstham.website.frontend.controller.administration;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("AdminHomeController")
public class HomeController {
    @RequestMapping(value = "/administration", name = "admin-home", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String homepage() {
        return "administration/home/home";
    }
}

