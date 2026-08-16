package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/** DTO for {@link cricket.merstham.graphql.entity.LeagueDuckTakersStatisticEntity} */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeagueDuckTakersStatistic implements Serializable {
    @Serial private static final long serialVersionUID = 5244406873714756048L;

    @JsonProperty private Integer id;
    @JsonProperty private String name;
    @JsonProperty private Long matches;
    @JsonProperty private Long wickets;
    @JsonProperty private Long ducks;
    @JsonProperty private Long goldenDucks;
    @JsonProperty private BigDecimal percentageDucks;
}
