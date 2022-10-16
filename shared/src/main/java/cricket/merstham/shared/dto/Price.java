package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonSerialize
public class Price implements Serializable {
    @Serial private static final long serialVersionUID = -1278786712455646877L;

    @JsonProperty private LocalDate dateFrom;
    @JsonProperty private LocalDate dateTo;
    @JsonProperty private BigDecimal price;
}
