package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/** DTO for {@link cricket.merstham.graphql.entity.CouponEntity} */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class Coupon implements Serializable {
    @Serial private static final long serialVersionUID = 8264562845475550837L;

    @JsonProperty private Integer id;
    @JsonProperty private String code;
    @JsonProperty private String ownerUserId;
    @JsonProperty private String description;
    @JsonProperty private BigDecimal value;
    @JsonProperty private Instant redeemDate;
    @JsonProperty private Integer appliedToOrderId;
}
