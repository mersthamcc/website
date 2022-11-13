package cricket.merstham.graphql.inputs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;

@JsonSerialize
@Getter
@Builder
public class AttributeInput {
    @JsonProperty private String key;
    @JsonProperty private JsonNode value;
}
