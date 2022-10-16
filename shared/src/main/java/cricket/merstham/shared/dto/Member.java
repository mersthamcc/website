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
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class Member implements Serializable {
    private static final long serialVersionUID = -5200325799993222375L;

    @JsonProperty private Integer id;
    @JsonProperty private String type;
    @JsonProperty private Instant registrationDate;
    @JsonProperty private String ownerUserId;
    @JsonProperty private List<MemberAttribute> attributes;
    @JsonProperty private List<MemberSubscription> subscription;
}
