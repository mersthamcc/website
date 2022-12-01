package cricket.merstham.graphql.services;

import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayCricketService {

    private static final String BASE_URL = "https://www.play-cricket.com/api/v2";
    private static final String TEAMS_ENDPOINT = "/teams.json";
    private static final String MATCHES_ENDPOINT = "/matches.json";
    private static final String MATCH_DETAIL_ENDPOINT = "/match_detail.json";
    private static final String FROM_ENTRY_DATE = "from_entry_date";
    private static final String END_ENTRY_DATE = "end_entry_date";
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

    public List<PlayCricketTeam> getTeams(Optional<Instant> lastUpdate, Instant now) {
        var request = createGetRequest(TEAMS_ENDPOINT, lastUpdate, now, Map.of(
                "site_id", Integer.toString(siteId)));
        var result = request.invoke(PlayCricketTeamResponse.class);

        return result
                .getTeams()
                .stream()
                .filter(t -> t.getSiteId() == siteId)
                .collect(Collectors.toList());
    }

    public List<PlayCricketMatch> getFixtures(Optional<Instant> lastUpdate, Instant now) {
        var request = createGetRequest(MATCHES_ENDPOINT, lastUpdate, now, Map.of(
                "site_id", Integer.toString(siteId)));

        return getPlayCricketMatches(request);
    }

    public List<PlayCricketMatch> getFixtures(int season) {
        var request = createGetRequest(MATCHES_ENDPOINT, Optional.empty(), Instant.now(), Map.of(
                "site_id", Integer.toString(siteId),
                "season", Integer.toString(season)));

        return getPlayCricketMatches(request);
    }

    private List<PlayCricketMatch> getPlayCricketMatches(Invocation request) {
        var result = request.invoke(PlayCricketMatchSummaryResponse.class);

        result.getMatches().forEach(playCricketMatch -> {
            var detailRequest = createGetRequest(
                    MATCH_DETAIL_ENDPOINT,
                    Optional.empty(),
                    null,
                    Map.of(
                            "match_id", Integer.toString(playCricketMatch.getId())));
            var detail = detailRequest.invoke(JsonNode.class);

            var home = Objects.equals(playCricketMatch.getHomeClubId(), siteId);
            playCricketMatch
                    .setDetails(detail.get("match_details").get(0))
                    .setTeamId(home ? playCricketMatch.getHomeTeamId() : playCricketMatch.getAwayTeamId())
                    .setHomeAway(home ? "HOME" : "AWAY");
        });
        return result.getMatches();
    }

    private Invocation createGetRequest(
            String endpoint,
            Optional<Instant> lastUpdate,
            Instant now,
            Map<String, String> queryParams) {
        var builder = UriBuilder
                .fromUri(BASE_URL).path(endpoint)
                .queryParam("api_token", apiToken);
        lastUpdate.ifPresent(l -> builder
                .queryParam(FROM_ENTRY_DATE, LocalDate.ofInstant(l, ZoneId.systemDefault()).format(formatter))
                .queryParam(END_ENTRY_DATE, LocalDate.ofInstant(now, ZoneId.systemDefault()).format(formatter)));
        queryParams.forEach((k, v) -> builder.queryParam(k, v));
        return client.target(builder)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .buildGet();
    }
}
