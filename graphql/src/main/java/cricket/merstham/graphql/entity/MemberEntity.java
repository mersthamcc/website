package cricket.merstham.graphql.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Entity
@Table(
        name = "member",
        indexes = {@Index(name = "idx_member_owner_user_id", columnList = "owner_user_id")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "registration_date", nullable = false)
    private Instant registrationDate;

    @Column(name = "owner_user_id", nullable = false, length = 64)
    private String ownerUserId;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "primaryKey.member",
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    private List<MemberAttributeEntity> attributes;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "primaryKey.member",
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    private List<MemberSubscriptionEntity> subscription;
}
