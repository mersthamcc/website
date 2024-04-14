package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberCategory implements Serializable {
    @Serial private static final long serialVersionUID = -430962754454769790L;

    @JsonProperty private Integer id;
    @JsonProperty private String key;
    @JsonProperty private String registrationCode;
    @JsonProperty private int sortOrder;
    @JsonProperty private List<MemberCategoryFormSection> form;
    @JsonProperty private List<PriceListItem> priceListItem;
}
