package cricket.merstham.shared.dto;

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
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Pass implements Serializable {
    @Serial private static final long serialVersionUID = 7451773166072730917L;

    @JsonProperty private String type;
    @JsonProperty private String serialNumber;
    @JsonProperty private String content;
}
