package cricket.merstham.graphql.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.text.MessageFormat.format;

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

    @Column(name = "cancelled")
    private Instant cancelled;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column(name = "last_updated")
    private Instant lastUpdated;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "member_identifier", joinColumns = @JoinColumn(name = "member_id"))
    private Map<String, String> identifiers = new HashMap<>();

    @Transient
    public String getStringAttribute(String key) {
        return attributes.stream()
                .filter(a -> a.getDefinition().getKey().equals(key))
                .findFirst()
                .orElseThrow()
                .getValue()
                .asText();
    }

    @Transient
    public MemberAttributeEntity getAttributeByName(String name) {
        return attributes.stream()
                .filter(a -> a.getDefinition().getKey().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Transient
    public MemberSubscriptionEntity getMostRecentSubscription() {
        return subscription.stream()
                .max(
                        Comparator.comparing(MemberSubscriptionEntity::getYear)
                                .thenComparing(MemberSubscriptionEntity::getAddedDate))
                .orElseThrow();
    }

    @Transient
    public MemberEntity addSubscription(MemberSubscriptionEntity entity) {
        if (subscription == null) {
            subscription = new ArrayList<>();
        }
        subscription.add(entity);
        return this;
    }

    @Transient
    public long getPassUpdateEpochSecond() {
        return getLastUpdated().getEpochSecond();
    }

    public String getFullName() {
        return format(
                "{0} {1}", getStringAttribute("given-name"), getStringAttribute("family-name"));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MemberEntity that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uuid);
    }
}
