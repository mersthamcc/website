package cricket.merstham.graphql.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

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
                .filter(p -> today.isAfter(p.getDateFrom()) && today.isBefore(p.getDateTo()))
                .findFirst()
                .orElse(PricelistEntity.builder().price(BigDecimal.ZERO).build())
                .getPrice();
    }
}
