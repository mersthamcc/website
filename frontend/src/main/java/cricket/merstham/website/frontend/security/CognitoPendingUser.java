package cricket.merstham.website.frontend.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class CognitoPendingUser implements Serializable {
    @Serial private static final long serialVersionUID = -637404223903477834L;

    private String userId;
    private String destination;
    private String attributeName;
    private String confirmationMedium;
}
