package cricket.merstham.website.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping(value = "/", name = "home")
    public String homepage() {
        return "home/home";
    }
}
