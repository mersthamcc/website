package cricket.merstham.graphql.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayCricketMatch {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("status")
    private String status;

    @JsonProperty("published")
    private String published;

    @JsonProperty("last_updated")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate lastUpdated;

    @JsonProperty("league_name")
    private String leagueName;

    @JsonProperty("league_id")
    private Integer leagueId;

    @JsonProperty("competition_name")
    private String competitionName;

    @JsonProperty("competition_id")
    private Integer competitionId;

    @JsonProperty("competition_type")
    private String competitionType;

    @JsonProperty("match_type")
    private String matchType;

    @JsonProperty("game_type")
    private String gameType;

    @JsonProperty("season")
    private String season;

    @JsonProperty("match_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate matchDate;

    @JsonProperty("match_time")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime matchTime;

    @JsonProperty("ground_name")
    private String groundName;

    @JsonProperty("ground_id")
    private Integer groundId;

    @JsonProperty("ground_latitude")
    private String groundLatitude;

    @JsonProperty("ground_longitude")
    private String groundLongitude;

    @JsonProperty("home_club_name")
    private String homeClubName;

    @JsonProperty("home_team_name")
    private String homeTeamName;

    @JsonProperty("home_team_id")
    private Integer homeTeamId;

    @JsonProperty("home_club_id")
    private Integer homeClubId;

    @JsonProperty("away_club_name")
    private String awayClubName;

    @JsonProperty("away_team_name")
    private String awayTeamName;

    @JsonProperty("away_team_id")
    private Integer awayTeamId;

    @JsonProperty("away_club_id")
    private Integer awayClubId;

    private JsonNode details;
    private String homeAway;
    private Integer teamId;

    @Override
    public String toString() {
        return "PlayCricketMatch{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", lastUpdated=" + lastUpdated +
                ", season='" + season + '\'' +
                ", homeClubName='" + homeClubName + '\'' +
                ", homeTeamName='" + homeTeamName + '\'' +
                ", awayClubName='" + awayClubName + '\'' +
                ", awayTeamName='" + awayTeamName + '\'' +
                ", homeAway='" + homeAway + '\'' +
                '}';
    }
}
