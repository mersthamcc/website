package cricket.merstham.website.frontend.controller;

import cricket.merstham.shared.dto.MemberCategory;
import cricket.merstham.website.frontend.service.MembershipService;
import cricket.merstham.website.frontend.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;

@Controller
public class AboutController {

    private final PageService pageService;
    private final MembershipService membershipService;

    @Autowired
    public AboutController(PageService pageService, MembershipService membershipService) {
        this.pageService = pageService;
        this.membershipService = membershipService;
    }

    @GetMapping(value = "/about", name = "about")
    public ModelAndView about() throws IOException {
        var about = pageService.about();
        var model = new HashMap<String, Object>();
        model.put("about", about);

        return new ModelAndView("about/about", model);
    }

    @GetMapping(value = "/about/fees", name = "fees")
    public ModelAndView fees() throws IOException {
        var fees = pageService.fees();
        var categories = membershipService.getMembershipCategories();
        var model = new HashMap<String, Object>();
        model.put("fees", fees);
        model.put(
                "categories",
                categories.stream()
                        .filter(category -> !category.getKey().equalsIgnoreCase("honorary"))
                        .sorted(Comparator.comparing(MemberCategory::getSortOrder))
                        .toList());

        return new ModelAndView("about/fees", model);
    }
}
