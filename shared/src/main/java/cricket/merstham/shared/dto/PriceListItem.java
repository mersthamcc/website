package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonSerialize
public class PriceListItem implements Serializable {
    @Serial private static final long serialVersionUID = -2679152375424526991L;

    @JsonProperty private int id;
    @JsonProperty private MemberCategory memberCategory;
    @JsonProperty private Integer minAge;
    @JsonProperty private Integer maxAge;
    @JsonProperty private String description;
    @JsonProperty private Boolean includesMatchFees;
    @JsonProperty private BigDecimal currentPrice;
    @JsonProperty private List<Price> price = new ArrayList<>();
    @JsonProperty private Boolean studentsOnly;
    @JsonProperty private Boolean parentDiscount;
    @JsonProperty private Boolean inclusiveKit;
    @JsonProperty private Integer sortOrder;
    @JsonProperty private String specificGender;
}
