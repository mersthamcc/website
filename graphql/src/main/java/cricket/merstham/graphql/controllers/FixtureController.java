package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.FixtureService;
import cricket.merstham.shared.dto.Fixture;
import cricket.merstham.shared.dto.League;
import cricket.merstham.shared.dto.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @QueryMapping
    public List<Fixture> fixtures(@Argument("season") int season) {
        return fixtureService.getFixtures(season);
    }

    @QueryMapping
    public List<Fixture> allFixturesForTeam(@Argument("id") int id) {
        return fixtureService.getFixturesForTeam(id);
    }

    @SchemaMapping(typeName = "Team", field = "fixtures")
    public List<Fixture> fixturesByTeam(Team team, @Argument("season") int season) {
        return fixtureService.getFixtures(season, team.getId());
    }

    @SchemaMapping(typeName = "Team", field = "league")
    public List<League> leaguesForTeam(Team team, @Argument("season") int season) {
        return fixtureService.getLeaguesForTeam(season, team.getId());
    }

    @QueryMapping
    public Team team(@Argument("id") int id) {
        return fixtureService.getTeam(id);
    }

    @QueryMapping
    public List<Integer> fixtureArchiveSeasons() {
        return fixtureService.getAllFixtureSeasons();
    }

    @QueryMapping("thisWeeksSelection")
    public List<Fixture> thisWeeksSelection() {
        return fixtureService.getThisWeeksSelection();
    }

    @MutationMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Fixture> refreshFixtures(@Argument("season") int season) {
        return fixtureService.refreshFixtures(season);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Team> refreshTeams() {
        fixtureService.refreshTeams();
        return fixtureService.getActiveTeams();
    }
}
