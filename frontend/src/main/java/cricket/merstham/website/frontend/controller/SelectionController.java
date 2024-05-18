package cricket.merstham.website.frontend.controller;

import cricket.merstham.shared.dto.Fixture;
import cricket.merstham.website.frontend.service.FixtureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;

@Controller
public class SelectionController {

    private final FixtureService fixtureService;

    @Autowired
    public SelectionController(FixtureService fixtureService) {
        this.fixtureService = fixtureService;
    }

    @GetMapping(path = "/selection", name = "selection")
    public ModelAndView getSelection() {
        try {
            var model = new HashMap<String, Object>();
            var selection = fixtureService.getSelection();
            model.put("selection", selection);
            model.put(
                    "dates", selection.stream().map(Fixture::getDate).distinct().sorted().toList());
            return new ModelAndView("selection/selection", model);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
