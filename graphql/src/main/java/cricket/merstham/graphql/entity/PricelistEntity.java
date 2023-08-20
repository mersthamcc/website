package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(
        name = "pricelist",
        indexes = {
            @Index(name = "idx_pricelist_item_date_from_date_to", columnList = "date_from, date_to")
        })
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricelistEntity implements Serializable {
    @Serial private static final long serialVersionUID = 2601456956606543549L;

    @EmbeddedId private PricelistEntityId primaryKey;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PricelistEntity that = (PricelistEntity) o;
        return primaryKey != null && Objects.equals(primaryKey, that.primaryKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(primaryKey);
    }

    @Transient
    public PricelistItemEntity getPricelistItem() {
        return primaryKey.getPricelistItem();
    }

    @Transient
    public LocalDate getDateFrom() {
        return primaryKey.getDateFrom();
    }

    @Transient
    public LocalDate getDateTo() {
        return primaryKey.getDateTo();
    }
}
