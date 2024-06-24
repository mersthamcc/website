package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/** DTO for {@link cricket.merstham.graphql.entity.FantasyPlayerStatisticEntity} */
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FantasyPlayerStatistic implements Serializable {
    @Serial private static final long serialVersionUID = 1019611225157176144L;

    @JsonProperty private final Integer year;
    @JsonProperty private final Integer id;
    @JsonProperty private final String name;
    @JsonProperty private final Long runs;
    @JsonProperty private final Long wickets;
    @JsonProperty private final Long catches;
    @JsonProperty private final Long maidens;
    @JsonProperty private final Long fifties;
    @JsonProperty private final Long hundreds;
    @JsonProperty private final Long ducks;
    @JsonProperty private final Long concededRuns;
    @JsonProperty private final BigDecimal overs;
}
