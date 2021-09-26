package cricket.merstham.website.frontend.model.datatables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.Optional;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class SspResponse<T extends SspBaseResponseData> {

    @JsonProperty
    private final int draw;
    @JsonProperty
    private final int recordsTotal;
    @JsonProperty
    private final int recordsFiltered;
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    private final Optional<List<String>> error;

    @JsonProperty
    private final List<T> data;

    protected SspResponse(int draw, int recordsTotal, int recordsFiltered, Optional<List<String>> error, List<T> data) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.error = error;
        this.data = data;
    }

    public int getDraw() {
        return draw;
    }

    public int getRecordsTotal() {
        return recordsTotal;
    }

    public int getRecordsFiltered() {
        return recordsFiltered;
    }

    public Optional<List<String>> getError() {
        return error;
    }

    public List<T> getData() {
        return data;
    }

    public static <T extends SspBaseResponseData> Builder<T> builder(Class<T> clazz) {
        return new Builder<>();
    }

    public static class Builder<T extends SspBaseResponseData> {
        private int draw;
        private int recordsTotal;
        private int recordsFiltered;
        private Optional<List<String>> error = Optional.empty();
        private List<T> data;

        public Builder<T> withDraw(int draw) {
            this.draw = draw;
            return this;
        }

        public Builder<T> withRecordsTotal(int recordsTotal) {
            this.recordsTotal = recordsTotal;
            return this;
        }

        public Builder<T> withRecordsFiltered(int recordsFiltered) {
            this.recordsFiltered = recordsFiltered;
            return this;
        }

        public Builder<T> withError(List<String> error) {
            this.error = Optional.of(error);
            return this;
        }

        public Builder<T> withData(List<T> data) {
            this.data = data;
            return this;
        }

        public SspResponse<T> build() {
            return new SspResponse<>(draw, recordsTotal, recordsFiltered, error, data);
        }
    }
}
