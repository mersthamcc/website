package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(
        name = "member_attendance",
        indexes = {@Index(name = "member_attendance_member_id", columnList = "member_id")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberAttendanceEntity {
    @Id
    @Column(name = "id", nullable = false, length = Integer.MAX_VALUE)
    private String id;

    @NotNull
    @Column(name = "\"time\"", nullable = false)
    private Instant time;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "member_id")
    @ToString.Exclude
    private MemberEntity member;

    @Column(name = "non_member_name", length = Integer.MAX_VALUE)
    private String nonMemberName;

    @Column(name = "event", length = Integer.MAX_VALUE)
    private String event;
}
