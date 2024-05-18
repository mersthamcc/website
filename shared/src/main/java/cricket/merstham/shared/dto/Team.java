package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.beans.Transient;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class Team implements Serializable {

    @Serial private static final long serialVersionUID = 8541193766005290892L;
    private static final String ACTIVE = "active";

    @JsonProperty("id")
    private int id;

    @JsonProperty("sort_order")
    private long sortOrder;

    @JsonProperty("name")
    private String name;

    @JsonProperty("status")
    private String status;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("include_in_selection")
    private boolean includeInSelection;

    @JsonProperty("captain")
    private Player captain;

    @JsonProperty("league")
    private List<League> league;

    @JsonProperty("fixture")
    private List<Fixture> fixtures;

    @Transient
    public boolean isActive() {
        return status.equals(ACTIVE);
    }
}
