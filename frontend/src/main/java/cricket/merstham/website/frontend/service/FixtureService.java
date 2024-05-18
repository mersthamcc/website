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
import java.util.List;

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

        return result.getData().getActiveTeams().stream()
                .map(t -> modelMapper.map(t, Team.class))
                .toList();
    }

    public Team getFixtures(int season, int teamId) throws IOException {
        FixturesByTeamQuery query =
                FixturesByTeamQuery.builder().season(season).team(teamId).build();
        Response<FixturesByTeamQuery.Data> result = graphService.executeQuery(query);

        return modelMapper.map(result.getData().getTeam(), Team.class);
    }

    public List<Fixture> allFixturesForTeam(int teamId) throws IOException {
        AllFixturesForTeamQuery query = AllFixturesForTeamQuery.builder().id(teamId).build();

        Response<AllFixturesForTeamQuery.Data> result = graphService.executeQuery(query);

        return result.getData().getAllFixturesForTeam().stream()
                .map(f -> modelMapper.map(f, Fixture.class))
                .toList();
    }

    public Team getTeam(int teamId) throws IOException {
        GetTeamQuery query = GetTeamQuery.builder().id(teamId).build();
        Response<GetTeamQuery.Data> result = graphService.executeQuery(query);
        return modelMapper.map(result.getData().getTeam(), Team.class);
    }

    public List<Fixture> getSelection() throws IOException {
        var query = ThisWeeksSelectionQuery.builder().build();
        Response<ThisWeeksSelectionQuery.Data> result = graphService.executeQuery(query);
        return result.getData().getThisWeeksSelection().stream()
                .map(f -> modelMapper.map(f, Fixture.class))
                .toList();
    }
}
