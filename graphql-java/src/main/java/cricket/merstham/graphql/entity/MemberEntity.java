package cricket.merstham.graphql.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member", indexes = {
        @Index(name = "idx_member_owner_user_id", columnList = "owner_user_id")
})
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "primaryKey.member", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<MemberAttributeEntity> attributes = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "primaryKey.member", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<MemberSubscriptionEntity> subscription = new ArrayList<>();

}