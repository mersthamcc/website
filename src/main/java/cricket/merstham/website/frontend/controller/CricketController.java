package cricket.merstham.website.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CricketController {
    @RequestMapping(path = "/fixtures", name = "fixtures", method = RequestMethod.GET)
    public String fixtures() {
        return "home/home";
    }

    @RequestMapping(path = "/results", name = "results", method = RequestMethod.GET)
    public String results() {
        return "home/home";
    }

    @RequestMapping(
            path = "/results/{year:[\\d]{4}}",
            name = "results-for-year",
            method = RequestMethod.GET)
    public String resultsForYear(@PathVariable int year) {
        return "home/home";
    }
}
