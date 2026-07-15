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
public class MediaOrigin implements Serializable {
    @Serial private static final long serialVersionUID = 4996696307955784060L;

    @JsonProperty private String type;
    @JsonProperty private String source;
    @JsonProperty private String format;
}
