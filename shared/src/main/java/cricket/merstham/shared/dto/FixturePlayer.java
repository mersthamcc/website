package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/** DTO for {@link cricket.merstham.graphql.entity.FixturePlayerEntity} */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class FixturePlayer implements Serializable {
    @Serial private static final long serialVersionUID = 7466206303593526591L;

    @JsonProperty private Fixture fixture;
    @JsonProperty private Integer playerId;
    @JsonProperty private Integer position;
    @JsonProperty private String name;
    @JsonProperty private Boolean captain;
    @JsonProperty private Boolean wicketKeeper;
}
