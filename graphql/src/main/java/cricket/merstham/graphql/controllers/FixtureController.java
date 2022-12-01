package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.FixtureService;
import cricket.merstham.shared.dto.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class FixtureController {

    private final FixtureService fixtureService;

    @Autowired
    public FixtureController(FixtureService fixtureService) {
        this.fixtureService = fixtureService;
    }

    @QueryMapping
    public List<Team> teams() {
        return fixtureService.getAllTeams();
    }

    @QueryMapping
    public List<Team> activeTeams() {
        return fixtureService.getActiveTeams();
    }
}
