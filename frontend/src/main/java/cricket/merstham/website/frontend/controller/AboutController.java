package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;

@Controller
public class AboutController {

    private final PageService pageService;

    @Autowired
    public AboutController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping(value = "/about", name = "about")
    public ModelAndView about() throws IOException {
        var about = pageService.about();
        var model = new HashMap<String, Object>();
        model.put("about", about);

        return new ModelAndView("about/about", model);
    }
}
