package cricket.merstham.shared.dto;

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
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import static cricket.merstham.shared.extensions.OrdinalExtensions.getOrdinal;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class League implements Serializable {

    @Serial private static final long serialVersionUID = -6241106754547406721L;

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("last_update")
    private Instant lastUpdate;

    @JsonProperty("table")
    private JsonNode table;

    @Transient
    public String getTeamRank(int teamId) {
        int rank = 1;
        for (var node : table.get("values")) {
            if (node.get("team_id").asInt() == teamId) {
                return getOrdinal(rank);
            }
            rank++;
        }
        return "";
    }

    @Transient
    public int getTeamPoints(int teamId) {
        String column = null;
        var fields = table.get("headings").fields();
        while (fields.hasNext() && isNull(column)) {
            var field = fields.next();
            if (field.getValue().isTextual() && field.getValue().asText().equals("Pts")) {
                column = field.getKey();
            }
        }
        if (nonNull(column)) {
            for (var node : table.get("values")) {
                if (node.get("team_id").asInt() == teamId) {
                    return node.get(column).asInt();
                }
            }
        }
        return 0;
    }
}
