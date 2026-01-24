package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/** DTO for {@link cricket.merstham.graphql.entity.PricelistItemInfoEntity} */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class PricelistItemInfo implements Serializable {
    @JsonProperty private Long pricelistItemId;
    @JsonProperty private String key;
    @JsonProperty private String icon;
    @JsonProperty private String description;
}
