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
public class Arguments implements Serializable {
    @Serial private static final long serialVersionUID = -9193553025143569380L;

    @JsonProperty("play_from_url")
    private String playFromUrl;
}
