package cricket.merstham.shared.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

@Data
public class MemberAttribute implements Serializable {
    private static final long serialVersionUID = 8683188972048441996L;

    private Long memberId;
    private AttributeDefinition definition;
    private Instant createdDate;
    private Instant updatedDate;
    private JsonNode value;
}
