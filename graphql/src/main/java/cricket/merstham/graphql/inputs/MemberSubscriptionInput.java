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
public class MemberSubscriptionInput {
    @JsonProperty private LocalDate addedDate;

    @JsonProperty private int year;

    @JsonProperty private BigDecimal price;

    @JsonProperty private int priceListItemId;

    @JsonProperty private int orderId;
}
