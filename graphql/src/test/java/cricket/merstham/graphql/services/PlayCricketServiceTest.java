package cricket.merstham.graphql.services;

import cricket.merstham.graphql.dto.PlayCricketTeam;
import cricket.merstham.graphql.dto.PlayCricketTeamResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class PlayCricketServiceTest {

    private static final String API_TOKEN = UUID.randomUUID().toString();
    private static final int SITE_ID = 1234;
    private static final int OTHER_SITE_ID = 4321;
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final List<PlayCricketTeam> TEAMS =
            List.of(
                    PlayCricketTeam.builder()
                            .id(RANDOM.nextInt())
                            .siteId(SITE_ID)
                            .teamName("Sat 1st XI")
                            .lastUpdated(LocalDate.now())
                            .nickname("")
                            .otherTeamName("")
                            .teamCaptain(Integer.toString(RANDOM.nextInt()))
                            .status("active")
                            .build(),
                    PlayCricketTeam.builder()
                            .id(RANDOM.nextInt())
                            .siteId(OTHER_SITE_ID)
                            .teamName("Sat 1st XI")
                            .lastUpdated(LocalDate.now().minus(1, ChronoUnit.DAYS))
                            .nickname("")
                            .otherTeamName("")
                            .teamCaptain(Integer.toString(RANDOM.nextInt()))
                            .status("active")
                            .build(),
                    PlayCricketTeam.builder()
                            .id(RANDOM.nextInt())
                            .siteId(SITE_ID)
                            .teamName("Sat 2nd XI")
                            .lastUpdated(LocalDate.now().minus(2, ChronoUnit.DAYS))
                            .nickname("")
                            .otherTeamName("")
                            .teamCaptain(Integer.toString(RANDOM.nextInt()))
                            .status("active")
                            .build(),
                    PlayCricketTeam.builder()
                            .id(RANDOM.nextInt())
                            .siteId(SITE_ID)
                            .teamName("Sun 1st XI")
                            .lastUpdated(LocalDate.now().minus(3, ChronoUnit.DAYS))
                            .nickname("")
                            .otherTeamName("")
                            .teamCaptain(Integer.toString(RANDOM.nextInt()))
                            .status("active")
                            .build(),
                    PlayCricketTeam.builder()
                            .id(RANDOM.nextInt())
                            .siteId(SITE_ID)
                            .teamName("Sun 2nd XI")
                            .lastUpdated(LocalDate.now().minus(4, ChronoUnit.DAYS))
                            .nickname("")
                            .otherTeamName("")
                            .teamCaptain(Integer.toString(RANDOM.nextInt()))
                            .status("inactive")
                            .build(),
                    PlayCricketTeam.builder()
                            .id(RANDOM.nextInt())
                            .siteId(OTHER_SITE_ID)
                            .teamName("Sat 2nd XI")
                            .lastUpdated(LocalDate.now().minus(5, ChronoUnit.DAYS))
                            .nickname("")
                            .otherTeamName("")
                            .teamCaptain(Integer.toString(RANDOM.nextInt()))
                            .status("active")
                            .build(),
                    PlayCricketTeam.builder()
                            .id(RANDOM.nextInt())
                            .siteId(SITE_ID)
                            .teamName("Other")
                            .lastUpdated(LocalDate.now().minus(6, ChronoUnit.DAYS))
                            .nickname("")
                            .otherTeamName("Indoor Team")
                            .teamCaptain(Integer.toString(RANDOM.nextInt()))
                            .status("active")
                            .build());
    private final Client client = mock(Client.class);
    private final PlayCricketService service = new PlayCricketService(client, API_TOKEN, SITE_ID);

    @Test
    void shouldCorrectlyGetTeamsWithNoLastUpdate() {
        var expectedTeams = TEAMS.stream().filter(t -> t.getSiteId() == SITE_ID).toList();
        var webTarget = spy(WebTarget.class);
        var builder = spy(Invocation.Builder.class);
        var request = spy(Invocation.class);
        when(client.target(any(UriBuilder.class))).thenReturn(webTarget);
        when(webTarget.request()).thenReturn(builder);
        when(builder.accept(MediaType.APPLICATION_JSON_TYPE)).thenReturn(builder);
        when(builder.buildGet()).thenReturn(request);
        when(request.invoke(PlayCricketTeamResponse.class))
                .thenReturn(PlayCricketTeamResponse.builder().teams(TEAMS).build());

        var result = service.getTeams();

        assertThat(result.size(), equalTo(expectedTeams.size()));

        for (int i = 0; i < result.size(); i++) {
            assertThat(result.get(i), equalTo(expectedTeams.get(i)));
        }
    }
}
