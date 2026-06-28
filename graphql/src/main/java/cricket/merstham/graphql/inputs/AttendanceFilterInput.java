package cricket.merstham.graphql.inputs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@JsonSerialize
@Getter
@Builder
public class AttendanceFilterInput {
    @JsonProperty private LocalDate from;
    @JsonProperty private LocalDate to;
    @JsonProperty private Optional<List<String>> ageGroup;
    @JsonProperty private Optional<Boolean> includeUnregistered;
    @JsonProperty private Optional<Integer> memberId;
}
