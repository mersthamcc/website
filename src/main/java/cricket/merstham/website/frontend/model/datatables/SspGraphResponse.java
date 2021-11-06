package cricket.merstham.website.frontend.model.datatables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SspGraphResponse<T> implements Serializable {
    private static final long serialVersionUID = 20211031184100L;

    @JsonProperty
    private List<T> data;

    @JsonProperty
    private int recordsTotal;

    @JsonProperty
    private int recordsFiltered;
}
