package cricket.merstham.graphql.inputs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonSerialize
@Getter
@Builder
public class PaymentInput {
    @JsonProperty private String id;

    @JsonProperty private LocalDate date;

    @JsonProperty private String type;

    @JsonProperty private String reference;

    @JsonProperty private BigDecimal amount;

    @JsonProperty private BigDecimal processingFees;

    @JsonProperty private boolean collected;

    @JsonProperty private boolean reconciled;
}
