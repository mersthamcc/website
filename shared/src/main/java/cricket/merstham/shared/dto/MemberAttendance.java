package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberAttendance implements Serializable {
    @Serial private static final long serialVersionUID = 45760658552546644L;

    @JsonProperty private Integer id;
    @JsonProperty private Member member;
    @JsonProperty private Instant date;
    @JsonProperty private String reference;
    @JsonProperty private String event;
}
