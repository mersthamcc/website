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

/** DTO for {@link cricket.merstham.graphql.entity.LeagueDuckStatisticEntity} */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeagueDuckStatistic implements Serializable {
    @Serial private static final long serialVersionUID = -6681718781363861836L;

    @JsonProperty private Integer id;
    @JsonProperty private String name;
    @JsonProperty private Long matches;
    @JsonProperty private Long runs;
    @JsonProperty private Long ducks;

    @JsonProperty("percentage")
    private BigDecimal percentageDucks;

    @JsonProperty("not_out")
    private Long notOut;
}
