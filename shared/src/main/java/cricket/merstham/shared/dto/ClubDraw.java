package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/** DTO for {@link cricket.merstham.graphql.entity.ClubDrawEntity} */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClubDraw implements Serializable {
    @Serial private static final long serialVersionUID = -5522183219443662559L;

    @JsonProperty private Integer id;
    @JsonProperty private LocalDate drawDate;
    @JsonProperty private BigDecimal prizeFund;
    @JsonProperty private Set<ClubDrawPayment> clubDrawPayments = new LinkedHashSet<>();
    @JsonProperty private Set<ClubDrawWinner> clubDrawWinners = new LinkedHashSet<>();
}
