package cricket.merstham.graphql.dto.signage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class Event implements Serializable {
    @Serial private static final long serialVersionUID = -8869550883029063921L;

    @JsonProperty private Integer id;
    @JsonProperty private String start;
    @JsonProperty private String end;
    @JsonProperty private int duration;
    @JsonProperty private int priority;
    @JsonProperty private String recurrence;
    @JsonProperty private Source source;
}
