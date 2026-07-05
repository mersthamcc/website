package cricket.merstham.website.frontend.model.datatables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttendanceReportFilter {
    @JsonProperty private LocalDate from;
    @JsonProperty private LocalDate to;
    @JsonProperty private boolean includeUnregistered;
}
