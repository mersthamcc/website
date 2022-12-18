package cricket.merstham.graphql.services;

import com.fasterxml.jackson.databind.JsonNode;
import cricket.merstham.graphql.dto.PlayCricketLeague;
import cricket.merstham.graphql.dto.PlayCricketMatch;
import cricket.merstham.graphql.dto.PlayCricketMatchSummaryResponse;
import cricket.merstham.graphql.dto.PlayCricketTeam;
import cricket.merstham.graphql.dto.PlayCricketTeamResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.text.MessageFormat.format;

@Service
public class PlayCricketService {

    private static final String BASE_URL = "https://www.play-cricket.com/api/v2";
    private static final String TEAMS_ENDPOINT = "/teams.json";
    private static final String MATCHES_ENDPOINT = "/matches.json";
    private static final String MATCH_DETAIL_ENDPOINT = "/match_detail.json";
    private static final String LEAGUE_ENDPOINT = "/league_table.json";
    private static final String PLAYER_ENDPOINT = "/sites/{0,number,#}/players";
    private static final String FROM_ENTRY_DATE = "from_entry_date";
    private static final String END_ENTRY_DATE = "end_entry_date";
    public static final String SITE_ID = "site_id";
    private final Client client;
    private final String apiToken;
    private final int siteId;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    public PlayCricketService(
            @Named("play-cricket-client") Client client,
            @Value("${configuration.play-cricket.api-token}") String apiToken,
            @Value("${configuration.play-cricket.site-id}") int siteId) {
        this.client = client;
        this.apiToken = apiToken;
        this.siteId = siteId;
    }

    public List<PlayCricketTeam> getTeams() {
        var request = createGetRequest(TEAMS_ENDPOINT, Map.of(SITE_ID, Integer.toString(siteId)));
        var result = request.invoke(PlayCricketTeamResponse.class);

        return result.getTeams().stream()
                .filter(t -> t.getSiteId() == siteId)
                .collect(Collectors.toList());
    }

    public List<PlayCricketMatch> getFixtures(Optional<Instant> lastUpdate, Instant now) {
        var request =
                createGetRequest(
                        MATCHES_ENDPOINT,
                        lastUpdate,
                        now,
                        Map.of(SITE_ID, Integer.toString(siteId)));

        return getPlayCricketMatches(request);
    }

    public List<PlayCricketMatch> getFixtures(int season) {
        var request =
                createGetRequest(
                        MATCHES_ENDPOINT,
                        Map.of(
                                SITE_ID,
                                Integer.toString(siteId),
                                "season",
                                Integer.toString(season)));

        return getPlayCricketMatches(request);
    }

    public PlayCricketLeague getLeague(int leagueId) {
        var request =
                createGetRequest(
                        LEAGUE_ENDPOINT, Map.of("division_id", Integer.toString(leagueId)));

        return request.invoke(PlayCricketLeague.class);
    }

    public Map<Integer, JsonNode> getPlayers() {
        var request =
                createGetRequest(
                        format(PLAYER_ENDPOINT, siteId),
                        Map.of(
                                "include_everyone", "yes",
                                "include_historic", "yes"));
        var result = request.invoke(JsonNode.class);

        return StreamSupport.stream(result.get("players").spliterator(), true)
                .collect(Collectors.toMap(n -> n.get("member_id").asInt(), n -> n));
    }

    private List<PlayCricketMatch> getPlayCricketMatches(Invocation request) {
        var result = request.invoke(PlayCricketMatchSummaryResponse.class);

        result.getMatches()
                .forEach(
                        playCricketMatch -> {
                            var detailRequest =
                                    createGetRequest(
                                            MATCH_DETAIL_ENDPOINT,
                                            Map.of(
                                                    "match_id",
                                                    Integer.toString(playCricketMatch.getId())));
                            var detail = detailRequest.invoke(JsonNode.class);

                            var home = Objects.equals(playCricketMatch.getHomeClubId(), siteId);
                            playCricketMatch
                                    .setDetails(detail.get("match_details").get(0))
                                    .setTeamId(
                                            home
                                                    ? playCricketMatch.getHomeTeamId()
                                                    : playCricketMatch.getAwayTeamId())
                                    .setHomeAway(home ? "HOME" : "AWAY");
                        });
        return result.getMatches();
    }

    private Invocation createGetRequest(
            String endpoint,
            Optional<Instant> lastUpdate,
            Instant now,
            Map<String, String> queryParams) {
        Map<String, String> queryMap = new HashMap<>(queryParams);

        lastUpdate.ifPresent(
                l -> {
                    queryMap.put(
                            FROM_ENTRY_DATE,
                            LocalDate.ofInstant(l, ZoneId.systemDefault()).format(formatter));
                    queryMap.put(
                            END_ENTRY_DATE,
                            LocalDate.ofInstant(now, ZoneId.systemDefault()).format(formatter));
                });
        return createGetRequest(endpoint, queryMap);
    }

    private Invocation createGetRequest(String endpoint, Map<String, String> queryParams) {
        var builder = UriBuilder.fromUri(BASE_URL).path(endpoint).queryParam("api_token", apiToken);
        queryParams.forEach(builder::queryParam);
        return client.target(builder).request().accept(MediaType.APPLICATION_JSON_TYPE).buildGet();
    }
}
