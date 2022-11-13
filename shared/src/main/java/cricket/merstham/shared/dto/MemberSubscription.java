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
@JsonSerialize
@Accessors(chain = true)
public class MemberSubscription implements Serializable {
    @Serial private static final long serialVersionUID = 2389409542358861969L;

    @JsonProperty private PriceListItem priceListItem;
    @JsonProperty private BigDecimal price;
    @JsonProperty private LocalDate addedDate;
    @JsonProperty private Member member;
    @JsonProperty private int year;
    @JsonProperty private Order order;
    @JsonProperty private String category;
    @JsonProperty private RegistrationAction action;
}
