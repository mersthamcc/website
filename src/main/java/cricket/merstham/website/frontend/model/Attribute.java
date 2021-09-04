package cricket.merstham.website.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attribute {

    @JsonProperty
    private AttributeDefinition definition;
    @JsonProperty
    private LocalDateTime createdDate;
    @JsonProperty
    private LocalDateTime updatedDate;
    @JsonProperty
    private Object value;

    public AttributeDefinition getDefinition() {
        return definition;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public Object getValue() {
        return value;
    }
}
