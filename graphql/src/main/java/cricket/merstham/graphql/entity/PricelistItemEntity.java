package cricket.merstham.graphql.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "pricelist_item",
        indexes = {@Index(name = "idx_pricelist_item_category_id", columnList = "category_id")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricelistItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private MemberCategoryEntity memberCategory;

    @Column(name = "min_age", nullable = false)
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "includes_match_fees")
    private Boolean includesMatchFees;

    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = "primaryKey.pricelistItem",
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    private List<PricelistEntity> priceList = new ArrayList<>();

    @Transient
    public BigDecimal getCurrentPrice() {
        LocalDate today = LocalDate.now();
        return priceList.stream()
                .filter(
                        p ->
                                (today.isAfter(p.getDateFrom()) || today.isEqual(p.getDateFrom()))
                                        && (today.isBefore(p.getDateTo())
                                                || today.isEqual(p.getDateTo())))
                .findFirst()
                .orElse(PricelistEntity.builder().price(BigDecimal.ZERO).build())
                .getPrice();
    }
}
