package cricket.merstham.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PricelistItem implements Serializable {
    @Serial private static final long serialVersionUID = -2679152375424526991L;

    private int id;
    private MemberCategory memberCategory;
    private Integer minAge;
    private Integer maxAge;
    private String description;
    private Boolean includesMatchFees;
    private BigDecimal currentPrice;
}
