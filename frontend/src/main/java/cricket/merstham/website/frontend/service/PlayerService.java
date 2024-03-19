package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Response;
import cricket.merstham.shared.dto.Player;
import cricket.merstham.website.graph.player.PlayersQuery;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.isNull;

@Service
public class PlayerService {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerService.class);

    private final GraphService graphService;
    private final ModelMapper modelMapper;

    public PlayerService(GraphService graphService, ModelMapper modelMapper) {
        this.graphService = graphService;
        this.modelMapper = modelMapper;
    }

    public List<Player> getPlayers(
            String type, String term, Integer page, OAuth2AccessToken accessToken) {
        var query = new PlayersQuery();
        try {
            Response<PlayersQuery.Data> result = graphService.executeQuery(query, accessToken);
            return result.getData().getPlayers().stream()
                    .filter(player -> isNull(term) || player.getName().toLowerCase().contains(term))
                    .map(player -> modelMapper.map(player, Player.class))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
