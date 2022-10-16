package cricket.merstham.website.frontend.model.datatables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@JsonSerialize
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SspResponse<T extends SspResponseDataWrapper> {

    @JsonProperty private int draw;
    @JsonProperty private int recordsTotal;
    @JsonProperty private int recordsFiltered;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    private Optional<List<String>> error;

    @JsonProperty private List<T> data;
}
