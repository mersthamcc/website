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

/** DTO for {@link cricket.merstham.graphql.entity.ClubDrawWinnerEntity} */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClubDrawWinner implements Serializable {
    @Serial private static final long serialVersionUID = 6522915517603766349L;

    @JsonProperty private Integer id;
    @JsonProperty private Integer prizePercent;
    @JsonProperty private BigDecimal prizeAmount;
    @JsonProperty private LocalDate payoutDate;
    @JsonProperty private ClubDraw clubDraw;
    @JsonProperty private ClubDrawPayment clubDrawPayment;
}
