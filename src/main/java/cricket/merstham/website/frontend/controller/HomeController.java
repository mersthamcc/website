package cricket.merstham.website.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    @RequestMapping(value = "/", name = "home", method = RequestMethod.GET)
    public String homepage() {
        return "home/home";
    }
}
