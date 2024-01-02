package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonSerialize
public class Venue implements Serializable {
    @JsonProperty private String slug;
    @JsonProperty private String name;
    @JsonProperty private int sortOrder;
    @JsonProperty private String description;
    @JsonProperty private String directions;
    @JsonProperty private BigDecimal latitude;
    @JsonProperty private BigDecimal longitude;
    @JsonProperty private String address;
    @JsonProperty private String postCode;
    @JsonProperty private String marker;
    @JsonProperty private boolean showOnMenu;
    @JsonProperty private String aliasFor;
    @JsonProperty private Long playCricketId;
}
