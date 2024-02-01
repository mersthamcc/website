package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.beans.Transient;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

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
    @JsonProperty private List<MemberAttribute> attributes = new ArrayList<>();
    @JsonProperty private List<MemberSubscription> subscription = new ArrayList<>();

    @Transient
    public Map<String, JsonNode> getAttributeMap() {
        return isNull(attributes)
                ? Map.of()
                : getAttributes().stream()
                        .collect(
                                Collectors.toMap(
                                        a -> a.getDefinition().getKey(),
                                        MemberAttribute::getValue));
    }
}
