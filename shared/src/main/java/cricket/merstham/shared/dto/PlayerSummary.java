package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerSummary implements Serializable {
    @Serial private static final long serialVersionUID = 5912747746177145469L;

    @JsonProperty private Integer id;
    @JsonProperty private String name;
    @JsonProperty private Long fixturesLastYear;
    @JsonProperty private Long fixturesThisYear;
    @JsonProperty private LocalDate earliestDate;
}
