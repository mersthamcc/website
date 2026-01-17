package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.Transient;
import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import static java.util.Objects.nonNull;

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

    @Transient
    public boolean isEmpty() {
        return priceListItem == null
                || priceListItem.isEmpty()
                || priceListItem.stream().noneMatch(item -> nonNull(item.getCurrentPrice()));
    }

    @Transient
    public List<PriceListItem> getSortedPriceListItem() {
        return priceListItem.stream()
                .sorted(
                        Comparator.comparing(PriceListItem::getSortOrder)
                                .thenComparing(PriceListItem::getDescription))
                .toList();
    }
}
