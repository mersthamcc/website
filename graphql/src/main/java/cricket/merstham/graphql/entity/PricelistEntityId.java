package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricelistEntityId implements Serializable {
    @Serial private static final long serialVersionUID = 8600356922447089143L;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pricelist_item_id", nullable = false)
    private PricelistItemEntity pricelistItem;

    @Column(name = "date_from", nullable = false)
    private LocalDate dateFrom;

    @Column(name = "date_to", nullable = false)
    private LocalDate dateTo;
}
