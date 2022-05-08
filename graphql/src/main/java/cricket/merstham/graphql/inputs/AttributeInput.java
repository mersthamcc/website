package cricket.merstham.graphql.inputs;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.web.ProjectedPayload;

@ProjectedPayload
public interface AttributeInput {
    String getKey();

    JsonNode getValue();
}
