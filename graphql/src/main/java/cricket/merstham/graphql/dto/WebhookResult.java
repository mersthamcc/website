package cricket.merstham.graphql.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebhookResult {
    @JsonProperty private Integer id;
    @JsonProperty private String reference;
    @JsonProperty private int status;
    @JsonProperty private String message;
}
