package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.Instant;

@Getter
@Entity
@Immutable
@Table(name = "member_attendance_summary")
public class MemberAttendanceSummaryEntity {
    @Id
    @Column(name = "id", length = Integer.MAX_VALUE)
    private String id;

    @Column(name = "\"time\"")
    private Instant time;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "full_name", length = Integer.MAX_VALUE)
    private String fullName;

    @Column(name = "agegroup", length = Integer.MAX_VALUE)
    private String ageGroup;

    @Column(name = "event", length = Integer.MAX_VALUE)
    private String event;

    @Column(name = "registration_year")
    private Integer registrationYear;
}
