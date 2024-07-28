package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.beans.Transient;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static java.text.MessageFormat.format;

/** A DTO for the {@link cricket.merstham.graphql.entity.FixtureEntity} entity */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fixture implements Serializable {
    @Serial private static final long serialVersionUID = -5910536573668698659L;

    @JsonProperty("id")
    private int id;

    @JsonProperty("team")
    private Team team;

    @JsonProperty("opposition_team_id")
    private Integer oppositionTeamId;

    @JsonProperty("opposition")
    private String opposition;

    @JsonProperty("ground_id")
    private Integer groundId;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("home_away")
    private String homeAway;

    @JsonProperty("start")
    private LocalTime start;

    @JsonProperty("detail")
    private JsonNode detail;

    @JsonProperty("players")
    private List<FixturePlayer> players;

    private int focusTeam;

    @JsonIgnore
    @Transient
    public String getVenue() {
        return detail.at("/ground_name").asText();
    }

    @JsonIgnore
    @Transient
    public String getResult() {
        return detail.at("/result_description").asText();
    }

    @JsonIgnore
    @Transient
    public int getFirstInningsRuns() {
        return detail.at("/innings/0/runs").asInt();
    }

    @JsonIgnore
    @Transient
    public int getSecondInningsRuns() {
        return detail.at("/innings/1/runs").asInt();
    }

    @JsonIgnore
    @Transient
    public int getFirstInningsWickets() {
        return detail.at("/innings/0/wickets").asInt();
    }

    @JsonIgnore
    @Transient
    public int getSecondInningsWickets() {
        return detail.at("/innings/1/wickets").asInt();
    }

    @JsonIgnore
    @Transient
    public double getFirstInningsOvers() {
        return detail.at("/innings/0/overs").asDouble();
    }

    @JsonIgnore
    @Transient
    public double getSecondInningsOvers() {
        return detail.at("/innings/1/overs").asDouble();
    }

    @JsonIgnore
    @Transient
    public boolean getFirstInningsDeclared() {
        return detail.at("/innings/0/declared").asBoolean();
    }

    @JsonIgnore
    @Transient
    public boolean getSecondInningsDeclared() {
        return detail.at("/innings/1/declared").asBoolean();
    }

    @JsonIgnore
    @Transient
    public String getFirstInningsTeamName() {
        return detail.at("/innings/0/team_batting_name").asText("");
    }

    @JsonIgnore
    @Transient
    public String getSecondInningsTeamName() {
        return detail.at("/innings/1/team_batting_name").asText("");
    }

    @JsonIgnore
    @Transient
    public String getMatchType() {
        return detail.at("/competition_type").asText("");
    }

    @JsonIgnore
    @Transient
    public String getCompetitionName() {
        return detail.at("/competition_name").asText("");
    }

    @JsonIgnore
    @Transient
    public String getStartTime() {
        return detail.at("/match_time").asText("");
    }

    @JsonIgnore
    @Transient
    public boolean isIntraClub() {
        return detail.at("/home_club_id").asText().equals(detail.at("/away_club_id").asText(""));
    }

    @JsonIgnore
    @Transient
    public boolean isFriendly() {
        return detail.at("/competition_type").asText().equals("Friendly");
    }

    @JsonIgnore
    @Transient
    public int getBatsmanRuns(int innings, int batsman) {
        return detail.at(format("/innings/{0}/bat/{1}/runs", innings - 1, batsman - 1)).asInt();
    }

    @JsonIgnore
    @Transient
    public int getBatsmanFours(int innings, int batsman) {
        return detail.at(format("/innings/{0}/bat/{1}/fours", innings - 1, batsman - 1)).asInt();
    }

    @JsonIgnore
    @Transient
    public int getBatsmanSixes(int innings, int batsman) {
        return detail.at(format("/innings/{0}/bat/{1}/sixes", innings - 1, batsman - 1)).asInt();
    }

    @JsonIgnore
    @Transient
    public String getBatsmanName(int innings, int batsman) {
        return detail.at(format("/innings/{0}/bat/{1}/batsman_name", innings - 1, batsman - 1))
                .asText("");
    }

    @JsonIgnore
    @Transient
    public String getBatsmanHowOut(int innings, int batsman) {
        return detail.at(format("/innings/{0}/bat/{1}/how_out", innings - 1, batsman - 1))
                .asText("");
    }

    @JsonIgnore
    @Transient
    public String getBatsmanWicketBowler(int innings, int batsman) {
        return detail.at(format("/innings/{0}/bat/{1}/bowler_name", innings - 1, batsman - 1))
                .asText("");
    }

    @JsonIgnore
    @Transient
    public String getBatsmanWicketFielder(int innings, int batsman) {
        return detail.at(format("/innings/{0}/bat/{1}/fielder_name", innings - 1, batsman - 1))
                .asText("");
    }

    @JsonIgnore
    @Transient
    public String getBowlerName(int innings, int bowler) {
        return detail.at(format("/innings/{0}/bowl/{1}/bowler_name", innings - 1, bowler - 1))
                .asText("");
    }

    @JsonIgnore
    @Transient
    public int getBowlerRuns(int innings, int bowler) {
        return detail.at(format("/innings/{0}/bowl/{1}/runs", innings - 1, bowler - 1)).asInt();
    }

    @JsonIgnore
    @Transient
    public int getBowlerWickets(int innings, int bowler) {
        return detail.at(format("/innings/{0}/bowl/{1}/wickets", innings - 1, bowler - 1)).asInt();
    }

    @JsonIgnore
    @Transient
    public int getBowlerMaidens(int innings, int bowler) {
        return detail.at(format("/innings/{0}/bowl/{1}/maidens", innings - 1, bowler - 1)).asInt();
    }

    @JsonIgnore
    @Transient
    public int getBowlerWides(int innings, int bowler) {
        return detail.at(format("/innings/{0}/bowl/{1}/wides", innings - 1, bowler - 1)).asInt();
    }

    @JsonIgnore
    @Transient
    public int getBowlerNoBalls(int innings, int bowler) {
        return detail.at(format("/innings/{0}/bowl/{1}/no_balls", innings - 1, bowler - 1)).asInt();
    }

    @JsonIgnore
    @Transient
    public double getBowlerOvers(int innings, int bowler) {
        return detail.at(format("/innings/{0}/bowl/{1}/overs", innings - 1, bowler - 1)).asDouble();
    }

    @JsonIgnore
    @Transient
    public int getNumberOfBats(int innings) {
        return detail.at(format("/innings/{0}/bat", innings - 1)).size();
    }

    @JsonIgnore
    @Transient
    public int getNumberOfBowlers(int innings) {
        return detail.at(format("/innings/{0}/bowl", innings - 1)).size();
    }

    @JsonIgnore
    @Transient
    public int getInningsExtras(int innings) {
        return detail.at(format("/innings/{0}/total_extras", innings - 1)).asInt();
    }

    @JsonIgnore
    @Transient
    public int getInningsRuns(int innings) {
        return detail.at(format("/innings/{0}/runs", innings - 1)).asInt();
    }

    @JsonIgnore
    @Transient
    public int getInningsByes(int innings) {
        return detail.at(format("/innings/{0}/extra_byes", innings - 1)).asInt();
    }

    @JsonIgnore
    @Transient
    public int getInningsLegByes(int innings) {
        return detail.at(format("/innings/{0}/extra_leg_byes", innings - 1)).asInt();
    }

    @JsonIgnore
    @Transient
    public String getToss() {
        return detail.at("/toss").asText("");
    }

    @JsonIgnore
    @Transient
    public boolean inningsBattingPresent(int innings) {
        var inningsNode = detail.at(format("/innings/{0}/bat", innings - 1));
        return inningsNode.isArray() && !inningsNode.isEmpty();
    }

    @JsonIgnore
    @Transient
    public boolean inningsBowlingPresent(int innings) {
        var inningsNode = detail.at(format("/innings/{0}/bowl", innings - 1));
        return inningsNode.isArray() && !inningsNode.isEmpty();
    }

    @JsonIgnore
    @Transient
    public String getPath() {
        return format(
                "{0,number,####}/{1,number,#########}/{2,number,#########}",
                date.getYear(),
                team.getId(),
                id);
    }
}
