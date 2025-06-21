package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "club_draw_subscription")
public class ClubDrawSubscriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 64)
    @Column(name = "owner_user_id", length = 64)
    private String ownerUserId;

    @Column(name = "subscription_id", length = Integer.MAX_VALUE)
    private String subscriptionId;

    @Column(name = "create_date")
    private Instant createDate;

    @Column(name = "last_updated")
    private Instant lastUpdated;

    @Column(name = "active")
    private Boolean active;

    @OneToMany(mappedBy = "clubDrawSubscription")
    private Set<ClubDrawPaymentEntity> clubDrawPayments = new LinkedHashSet<>();
}
