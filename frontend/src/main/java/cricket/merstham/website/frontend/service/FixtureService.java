package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Response;
import cricket.merstham.shared.dto.Team;
import cricket.merstham.website.graph.ActiveTeamsQuery;
import cricket.merstham.website.graph.FixturesByTeamQuery;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
                .collect(Collectors.toList());
    }

    public Team getFixtures(int season, int teamId) throws IOException {
        FixturesByTeamQuery query =
                FixturesByTeamQuery.builder().season(season).team(teamId).build();
        Response<FixturesByTeamQuery.Data> result = graphService.executeQuery(query);

        return modelMapper.map(result.getData().getTeam(), Team.class);
    }
}
