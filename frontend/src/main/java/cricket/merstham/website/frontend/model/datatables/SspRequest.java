package cricket.merstham.website.frontend.model.datatables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class SspRequest {
    @JsonProperty private int draw;
    @JsonProperty private int start;
    @JsonProperty private int length;
    @JsonProperty private SspRequestSearch search;
    @JsonProperty private List<SspRequestOrder> order;
    @JsonProperty private List<SspRequestColumn> columns;
}
