package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Response;
import cricket.merstham.shared.dto.Fixture;
import cricket.merstham.shared.dto.Team;
import cricket.merstham.website.graph.ActiveTeamsQuery;
import cricket.merstham.website.graph.fixture.AllFixturesForTeamQuery;
import cricket.merstham.website.graph.fixture.FixturesByTeamQuery;
import cricket.merstham.website.graph.fixture.GetTeamQuery;
import cricket.merstham.website.graph.fixture.ThisWeeksSelectionQuery;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static cricket.merstham.website.frontend.helpers.GraphQLResultHelper.requireGraphData;
import static java.text.MessageFormat.format;

@Service
public class FixtureService {
    private static final Logger LOG = LoggerFactory.getLogger(FixtureService.class);

    private final GraphService graphService;
    private final ModelMapper modelMapper;

    @Autowired
    public FixtureService(GraphService graphService, ModelMapper modelMapper) {
        this.graphService = graphService;
        this.modelMapper = modelMapper;
    }

    public List<Team> getActiveTeams() throws IOException {
        ActiveTeamsQuery query = ActiveTeamsQuery.builder().build();
        Response<ActiveTeamsQuery.Data> result = graphService.executeQuery(query);

        return requireGraphData(
                        result,
                        ActiveTeamsQuery.Data::getActiveTeams,
                        () -> "Error getting active teams")
                .stream()
                .map(t -> modelMapper.map(t, Team.class))
                .toList();
    }

    public Team getFixtures(int season, int teamId) throws IOException {
        FixturesByTeamQuery query =
                FixturesByTeamQuery.builder().season(season).team(teamId).build();
        Response<FixturesByTeamQuery.Data> result = graphService.executeQuery(query);

        return modelMapper.map(
                requireGraphData(
                        result,
                        FixturesByTeamQuery.Data::getTeam,
                        () ->
                                format(
                                        "Error getting fixtures for team {0,number,#######} - season {1,number,####}",
                                        teamId,
                                        season)),
                Team.class);
    }

    public List<Fixture> allFixturesForTeam(int teamId) throws IOException {
        AllFixturesForTeamQuery query = AllFixturesForTeamQuery.builder().id(teamId).build();

        Response<AllFixturesForTeamQuery.Data> result = graphService.executeQuery(query);

        return requireGraphData(
                        result,
                        AllFixturesForTeamQuery.Data::getAllFixturesForTeam,
                        () -> format("Error getting fixtures for team {0,number,#######}", teamId))
                .stream()
                .map(f -> modelMapper.map(f, Fixture.class))
                .toList();
    }

    public Team getTeam(int teamId) throws IOException {
        GetTeamQuery query = GetTeamQuery.builder().id(teamId).build();
        Response<GetTeamQuery.Data> result = graphService.executeQuery(query);
        return modelMapper.map(result.getData().getTeam(), Team.class);
    }

    public List<Fixture> getSelection() throws IOException {
        var query = ThisWeeksSelectionQuery.builder().season(LocalDate.now().getYear()).build();
        Response<ThisWeeksSelectionQuery.Data> result = graphService.executeQuery(query);
        return requireGraphData(
                        result,
                        ThisWeeksSelectionQuery.Data::getThisWeeksSelection,
                        () -> "Error getting selection data")
                .stream()
                .map(
                        f -> {
                            var fixture = modelMapper.map(f, Fixture.class);
                            return fixture;
                        })
                .toList();
    }
}
