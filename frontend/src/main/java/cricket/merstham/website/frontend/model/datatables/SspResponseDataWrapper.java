package cricket.merstham.website.frontend.model.datatables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cricket.merstham.website.frontend.helpers.RoutesHelper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SspResponseDataWrapper<T> {
    @JsonProperty("DT_RowId")
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    protected Optional<String> rowId;

    @JsonProperty("DT_RowClass")
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    protected Optional<String> rowClass;

    @JsonProperty("DT_RowData")
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    protected Optional<Map<String, Object>> rowData;

    @JsonProperty("DT_RowAttr")
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    protected Optional<Map<String, String>> rowAttr;

    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    protected T data;

    private Optional<String> editRouteTemplate;
    private Optional<String> deleteRouteTemplate;
    private Function<T, Map<String, Object>> mapFunction;

    @JsonProperty
    public URI getEditLink() {
        return editRouteTemplate
                .map(route -> RoutesHelper.buildRoute(route, mapFunction.apply(data)))
                .orElse(null);
    }

    @JsonProperty
    public URI getDeleteLink() {
        return deleteRouteTemplate
                .map(route -> RoutesHelper.buildRoute(route, mapFunction.apply(data)))
                .orElse(null);
    }
}
