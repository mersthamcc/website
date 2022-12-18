package cricket.merstham.graphql.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonSerialize
public class PlayCricketTeam {
    @JsonProperty("id")
    private int id;

    @JsonProperty("status")
    private String status;

    @JsonProperty("last_updated")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate lastUpdated;

    @JsonProperty("site_id")
    private int siteId;

    @JsonProperty("team_name")
    private String teamName;

    @JsonProperty("other_team_name")
    private String otherTeamName;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("team_captain")
    private String teamCaptain;

    @Override
    public String toString() {
        return "PlayCricketTeam{"
                + "id="
                + id
                + ", status='"
                + status
                + '\''
                + ", teamName='"
                + teamName
                + '\''
                + ", otherTeamName='"
                + otherTeamName
                + '\''
                + '}';
    }
}
