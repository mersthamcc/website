package cricket.merstham.website.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CricketController {
    @GetMapping(path = "/fixtures", name = "fixtures")
    public String fixtures() {
        return "home/home";
    }

    @GetMapping(path = "/results", name = "results")
    public String results() {
        return "home/home";
    }

    @GetMapping(
            path = "/results/{year:[\\d]{4}}",
            name = "results-for-year")
    public String resultsForYear(@PathVariable int year) {
        return "home/home";
    }
}
