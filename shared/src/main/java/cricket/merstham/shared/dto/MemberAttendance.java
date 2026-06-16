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

/** DTO for {@link cricket.merstham.graphql.entity.MemberAttendanceEntity} */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberAttendance implements Serializable {
    @Serial private static final long serialVersionUID = 1974846518088177535L;

    @JsonProperty private String id;
    @JsonProperty private Instant time;
    @JsonProperty private Member member;
    @JsonProperty private String nonMemberName;
    @JsonProperty private String event;
}
