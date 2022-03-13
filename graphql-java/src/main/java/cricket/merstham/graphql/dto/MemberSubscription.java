package cricket.merstham.graphql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSubscription implements Serializable {
    @Serial
    private static final long serialVersionUID = 2389409542358861969L;

    private PricelistItem pricelistItem;
    private BigDecimal price;
    private Instant addedDate;
    private Member member;
    private int year;
    private Order order;
}
