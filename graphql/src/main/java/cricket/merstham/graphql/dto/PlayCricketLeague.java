package cricket.merstham.graphql.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.beans.Transient;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayCricketLeague {

    @JsonProperty("league_table")
    private JsonNode league;

    @Transient
    public int getId() {
        var node = league.at("/0/id");
        return node.isMissingNode() ? 0 : node.asInt();
    }

    @Transient
    public String getName() {
        var node = league.at("/0/name");
        return node.isMissingNode() ? "" : node.asText();
    }
}
