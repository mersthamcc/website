package cricket.merstham.shared.dto;

import com.fasterxml.jackson.databind.JsonNode;
import cricket.merstham.shared.types.AttributeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeDefinition implements Serializable {
    private static final long serialVersionUID = -1119329935470919963L;

    private Integer id;
    private String key;
    private AttributeType type;
    private JsonNode choices;
}
