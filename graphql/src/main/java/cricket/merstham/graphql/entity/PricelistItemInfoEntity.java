package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pricelist_item_info")
public class PricelistItemInfoEntity {
    @EmbeddedId private PricelistItemInfoEntityId id;

    @MapsId("pricelistItemId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pricelist_item_id", nullable = false)
    private PricelistItemEntity pricelistItem;

    @Column(name = "icon", length = Integer.MAX_VALUE)
    private String icon;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;
}
