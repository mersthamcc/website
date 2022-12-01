package cricket.merstham.shared.dto;

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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonSerialize
public class Team implements Serializable {

    @Serial
    private static final long serialVersionUID = 8541193766005290892L;
    private static final String ACTIVE = "active";

    @JsonProperty("id") private int id;
    @JsonProperty("sort_order") private long sortOrder;
    @JsonProperty("name") private String name;
    @JsonProperty("status") private String status;
    @JsonProperty("slug") private String slug;
    @JsonProperty("captain") private String captain;

    @Transient
    public boolean isActive() {
        return status.equals(ACTIVE);
    }
}
