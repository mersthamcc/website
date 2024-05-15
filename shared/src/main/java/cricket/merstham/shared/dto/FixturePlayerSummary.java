package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/** DTO for {@link cricket.merstham.graphql.entity.FixturePlayerSummaryEntity} */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class FixturePlayerSummary implements Serializable {
    @Serial private static final long serialVersionUID = -8693924517085721355L;

    @JsonProperty private int fixtureId;
    @JsonProperty private int playerId;
    @JsonProperty private Fixture fixture;
    @JsonProperty private Player player;
    @JsonProperty private Integer runs;
    @JsonProperty private Boolean out;
    @JsonProperty private Boolean dnb;
    @JsonProperty private Integer balls;
    @JsonProperty private Integer wickets;
    @JsonProperty private Integer overs;
    @JsonProperty private Integer maidens;
    @JsonProperty private Integer catches;
    @JsonProperty private Integer fours;
    @JsonProperty private Integer sixes;
}
