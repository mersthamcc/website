package cricket.merstham.website.frontend.controller.administration;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller("AdminHomeController")
public class HomeController {
    @GetMapping(value = "/administration", name = "admin-home")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String homepage() {
        return "administration/home/home";
    }
}

