package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class PricelistItemInfoEntityId implements Serializable {
    private static final long serialVersionUID = -8550137161035882532L;

    @NotNull
    @Column(name = "pricelist_item_id", nullable = false)
    private Long pricelistItemId;

    @Size(max = 48)
    @NotNull
    @Column(name = "key", nullable = false, length = 48)
    private String key;
}
