package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonSerialize
public class WebhookReceived {
    @JsonProperty private int id;
    @JsonProperty private Instant receivedDate;
    @JsonProperty private String type;
    @JsonProperty private String reference;
    @JsonProperty private JsonNode headers;
    @JsonProperty private JsonNode body;
    @JsonProperty private Boolean processed;
}
