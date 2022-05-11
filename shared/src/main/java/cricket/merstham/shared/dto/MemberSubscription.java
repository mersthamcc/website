package cricket.merstham.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberSubscription implements Serializable {
    @Serial private static final long serialVersionUID = 2389409542358861969L;

    private PricelistItem pricelistItem;
    private BigDecimal price;
    private LocalDate addedDate;
    private Member member;
    private int year;
    private Order order;
}
