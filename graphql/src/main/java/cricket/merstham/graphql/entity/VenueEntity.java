package cricket.merstham.graphql.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "venue")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueEntity {
    @Id
    @Column(name = "slug", nullable = false)
    @JsonProperty
    private String slug;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    @JsonProperty
    private String name;

    @NotNull
    @Column(name = "sort_order", nullable = false)
    @JsonProperty
    private int sortOrder;

    @NotNull
    @Column(name = "description", nullable = false)
    @JsonProperty
    private String description;

    @NotNull
    @Column(name = "directions", nullable = false)
    @JsonProperty
    private String directions;

    @Column(name = "latitude")
    @JsonProperty
    private BigDecimal latitude;

    @Column(name = "longitude")
    @JsonProperty
    private BigDecimal longitude;

    @Column(name = "address")
    @JsonProperty
    private String address;

    @Column(name = "post_code")
    @JsonProperty
    private String postCode;

    @Column(name = "marker")
    @JsonProperty
    private String marker;

    @Column(name = "show_on_menu")
    @JsonProperty
    private boolean showOnMenu;

    @Column(name = "alias_for")
    @JsonProperty
    private String aliasFor;

    @Column(name = "play_cricket_id")
    @JsonProperty
    private Long playCricketId;
}
