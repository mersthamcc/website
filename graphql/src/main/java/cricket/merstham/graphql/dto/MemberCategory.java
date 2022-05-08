package cricket.merstham.graphql.dto;

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

    private Integer id;
    private String key;
    private String registrationCode;
    private List<MemberCategoryFormSection> form;
    private List<PricelistItem> pricelistItem;
}
