package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.PlayerService;
import cricket.merstham.shared.dto.Player;
import cricket.merstham.shared.dto.PlayerSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class PlayerController {
    private final PlayerService service;

    @Autowired
    public PlayerController(PlayerService service) {
        this.service = service;
    }

    @QueryMapping
    public List<Player> players() {
        return service.getPlayers();
    }

    @QueryMapping
    public PlayerSummary playerSummary(@Argument int id) {
        return service.getPlayer(id);
    }
}
