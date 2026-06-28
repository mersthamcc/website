package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

/** DTO for {@link cricket.merstham.graphql.entity.MemberAttendanceSummary} */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberAttendanceSummary implements Serializable {
    @Serial private static final long serialVersionUID = -3761899607996185115L;

    @JsonProperty private String id;
    @JsonProperty private Instant time;
    @JsonProperty private Long memberId;
    @JsonProperty private String fullName;
    @JsonProperty private String ageGroup;
    @JsonProperty private String event;
    @JsonProperty private Integer registrationYear;
}
