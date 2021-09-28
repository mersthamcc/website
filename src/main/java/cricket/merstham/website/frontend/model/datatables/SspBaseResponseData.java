package cricket.merstham.website.frontend.model.datatables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Map;
import java.util.Optional;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class SspBaseResponseData {
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

    public Optional<String> getRowId() {
        return rowId;
    }

    public SspBaseResponseData setRowId(Optional<String> rowId) {
        this.rowId = rowId;
        return this;
    }

    public Optional<String> getRowClass() {
        return rowClass;
    }

    public SspBaseResponseData setRowClass(Optional<String> rowClass) {
        this.rowClass = rowClass;
        return this;
    }

    public Optional<Map<String, Object>> getRowData() {
        return rowData;
    }

    public SspBaseResponseData setRowData(Optional<Map<String, Object>> rowData) {
        this.rowData = rowData;
        return this;
    }

    public Optional<Map<String, String>> getRowAttr() {
        return rowAttr;
    }

    public SspBaseResponseData setRowAttr(Optional<Map<String, String>> rowAttr) {
        this.rowAttr = rowAttr;
        return this;
    }
}
