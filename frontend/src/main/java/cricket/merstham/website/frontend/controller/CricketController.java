package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.service.FixtureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import static java.util.Objects.isNull;

@Controller
public class CricketController {

    private static final Logger LOG = LoggerFactory.getLogger(CricketController.class);

    private final FixtureService fixtureService;

    @Autowired
    public CricketController(FixtureService fixtureService) {
        this.fixtureService = fixtureService;
    }

    @GetMapping(path = "/fixtures", name = "fixtures")
    public ModelAndView fixtures() {
        return getFixtures(LocalDate.now().getYear(), null);
    }

    @GetMapping(path = "/fixtures/{season:[\\d]{4}}/{teamId:[\\d]*}")
    public ModelAndView fixtures(@PathVariable int season, @PathVariable int teamId) {
        return getFixtures(season, teamId);
    }

    @GetMapping(path = "/results", name = "results")
    public String results() {
        return "home/home";
    }

    @GetMapping(path = "/results/{year:[\\d]{4}}", name = "results-for-year")
    public String resultsForYear(@PathVariable int year) {
        return "home/home";
    }

    private ModelAndView getFixtures(int season, Integer teamId) {
        try {
            var teams = fixtureService.getActiveTeams();
            var activeTeamId = isNull(teamId) ? teams.get(0).getId() : teamId.intValue();
            var activeTeam = fixtureService.getFixtures(season, activeTeamId);
            return new ModelAndView(
                    "cricket/fixtures",
                    Map.of(
                            "teams", teams,
                            "season", season,
                            "activeTeam", activeTeam));
        } catch (IOException e) {
            throw new RuntimeException("Error getting fixture data", e);
        }
    }
}
