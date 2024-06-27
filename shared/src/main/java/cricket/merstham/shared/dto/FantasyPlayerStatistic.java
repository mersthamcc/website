package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/** DTO for {@link cricket.merstham.graphql.entity.FantasyPlayerStatisticEntity} */
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FantasyPlayerStatistic implements Serializable {
    @Serial private static final long serialVersionUID = 1019611225157176144L;

    @JsonProperty private Integer year;
    @JsonProperty private Integer id;
    @JsonProperty private String name;
    @JsonProperty private Long matches;
    @JsonProperty private Long runs;
    @JsonProperty private Long wickets;
    @JsonProperty private Long catches;
    @JsonProperty private Long maidens;
    @JsonProperty private Long fifties;
    @JsonProperty private Long hundreds;
    @JsonProperty private Long ducks;
    @JsonProperty private Long concededRuns;
    @JsonProperty private Long notOut;
    @JsonProperty private BigDecimal overs;
}
