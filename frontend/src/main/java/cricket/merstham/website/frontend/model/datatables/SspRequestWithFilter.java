package cricket.merstham.website.frontend.model.datatables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class SspRequestWithFilter<T> extends SspRequest {
    @JsonProperty private T filter;
}
