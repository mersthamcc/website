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

    @GetMapping(path = "/fixtures/{season:[\\d]{4}}", name = "fixtures-for-season")
    public ModelAndView fixtures(@PathVariable int season) {
        return getFixtures(season, null);
    }

    @GetMapping(
            path = "/fixtures/{season:[\\d]{4}}/{teamId:[\\d]*}",
            name = "fixtures-for-season-team")
    public ModelAndView fixtures(@PathVariable int season, @PathVariable int teamId) {
        return getFixtures(season, teamId);
    }

    @GetMapping(
            path = "/fixtures/{season:[\\d]{4}}/{teamId:[\\d]*}/{fixtureId:[\\d]*}",
            name = "fixtures-details")
    public ModelAndView fixtureDetails(
            @PathVariable int season, @PathVariable int teamId, @PathVariable int fixtureId) {
        try {
            var teams = fixtureService.getActiveTeams();
            var activeTeam = fixtureService.getFixtures(season, teamId);
            var fixture =
                    activeTeam.getFixtures().stream()
                            .filter(f -> f.getId() == fixtureId)
                            .findFirst()
                            .orElseThrow();

            return new ModelAndView(
                    "cricket/fixture_detail",
                    Map.of(
                            "teams", teams,
                            "season", season,
                            "activeTeam", activeTeam,
                            "fixture", fixture));
        } catch (IOException e) {
            throw new RuntimeException("Error getting fixture data", e);
        }
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
