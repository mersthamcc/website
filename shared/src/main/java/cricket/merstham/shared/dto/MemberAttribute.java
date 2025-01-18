package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonSerialize
public class MemberAttribute implements Serializable {
    private static final long serialVersionUID = 8683188972048441996L;

    @JsonProperty private Long memberId;
    @JsonProperty private AttributeDefinition definition;
    @JsonProperty private Instant createdDate;
    @JsonProperty private Instant updatedDate;
    @JsonProperty private JsonNode value;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MemberAttribute that)) return false;
        return Objects.equals(memberId, that.memberId)
                && Objects.equals(definition, that.definition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, definition);
    }
}
