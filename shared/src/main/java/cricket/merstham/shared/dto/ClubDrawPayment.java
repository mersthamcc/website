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

/** DTO for {@link cricket.merstham.graphql.entity.ClubDrawPaymentEntity} */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClubDrawPayment implements Serializable {
    @Serial private static final long serialVersionUID = 4945180603940342433L;

    @JsonProperty private Integer id;
    @JsonProperty private LocalDate date;
    @JsonProperty private String paymentReference;
    @JsonProperty private BigDecimal amount;
    @JsonProperty private BigDecimal feesAmount;
    @JsonProperty private String accountingId;
    @JsonProperty private String feesAccountingId;
    @JsonProperty private String status;
    @JsonProperty private Boolean reconciled;
    @JsonProperty private String accountingError;
    @JsonProperty private ClubDraw includeInClubDraw;
    @JsonProperty private ClubDrawSubscription clubDrawSubscription;
}
