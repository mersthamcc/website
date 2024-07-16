package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {

    public static final String ROOT_URL = "/";

    private final PageService pageService;

    @Autowired
    public HomeController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping(value = "/", name = "home")
    public ModelAndView homepage() throws IOException {
        var home = pageService.home();
        Map<String, Object> model = new HashMap<>();
        model.put("home", home);
        return new ModelAndView("home/home", model);
    }
}
