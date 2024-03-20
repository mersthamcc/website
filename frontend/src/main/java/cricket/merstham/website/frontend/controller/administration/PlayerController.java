package cricket.merstham.website.frontend.controller.administration;

import com.fasterxml.jackson.annotation.JsonProperty;
import cricket.merstham.shared.dto.Player;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.PlayerService;
import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PlayerController {

    private final PlayerService service;

    @Autowired
    public PlayerController(PlayerService service) {
        this.service = service;
    }

    @GetMapping(
            produces = "application/json",
            value = "/administration/players/list",
            name = "admin-player-list")
    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public @ResponseBody Result list(
            @QueryParam("_type") String type,
            @QueryParam("term") String term,
            @QueryParam("page") Integer page,
            CognitoAuthentication cognitoAuthentication) {
        return Result.builder()
                .results(
                        service.getPlayers(
                                type, term, page, cognitoAuthentication.getOAuth2AccessToken()))
                .build();
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    private static class Result {
        @JsonProperty private List<Player> results;
    }
}
