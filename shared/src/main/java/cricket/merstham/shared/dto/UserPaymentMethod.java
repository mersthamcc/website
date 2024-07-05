package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/** DTO for {@link cricket.merstham.graphql.entity.UserPaymentMethodEntity} */
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPaymentMethod implements Serializable {
    @Serial private static final long serialVersionUID = 3781788689458946722L;

    @JsonProperty private Integer id;
    @JsonProperty private String userId;
    @JsonProperty private String provider;
    @JsonProperty private String type;
    @JsonProperty private String customerIdentifier;
    @JsonProperty private String methodIdentifier;
    @JsonProperty private LocalDateTime createDate;
    @JsonProperty private String status;
}
