package cricket.merstham.graphql.dto.spond;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchFeePayment {
    @JsonProperty private String id;
    @JsonProperty private LocalDate paymentDate;
    @JsonProperty private BigDecimal price;
    @JsonProperty private BigDecimal familyDiscount;
    @JsonProperty private BigDecimal gross;
    @JsonProperty private BigDecimal fees;
    @JsonProperty private BigDecimal net;
    @JsonProperty private String paymentDescription;
    @JsonProperty private String product;
    @JsonProperty private String memberName;
    @JsonProperty private String payerName;
    @JsonProperty private LocalDate payoutDate;
    @JsonProperty private URI link;
    @JsonProperty private String sageId;
    @JsonProperty private String feesSageId;
}
