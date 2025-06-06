package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.beans.Transient;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static cricket.merstham.shared.IdentifierConstants.EPOS_CUSTOMER_ID;
import static cricket.merstham.shared.IdentifierConstants.PLAYER_ID;
import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class Member implements Serializable {
    private static final long serialVersionUID = -5200325799993222375L;

    @JsonProperty private Integer id;
    @JsonProperty private String type;
    @JsonProperty private Instant registrationDate;
    @JsonProperty private String ownerUserId;
    @JsonProperty private String uuid;
    @JsonProperty private List<MemberAttribute> attributes = new ArrayList<>();
    @JsonProperty private List<MemberSubscription> subscription = new ArrayList<>();
    @JsonProperty private List<KeyValuePair> identifiers = new ArrayList<>();

    @Transient
    public Map<String, JsonNode> getAttributeMap() {
        return isNull(attributes)
                ? Map.of()
                : getAttributes().stream()
                        .collect(
                                Collectors.toMap(
                                        a -> a.getDefinition().getKey(),
                                        MemberAttribute::getValue));
    }

    @Transient
    public String getPlayerId() {
        return identifiers.stream()
                .filter(i -> i.getKey().equals(PLAYER_ID))
                .findFirst()
                .map(p -> p.getValue())
                .orElse(null);
    }

    @Transient
    public String getEposId() {
        return identifiers.stream()
                .filter(i -> i.getKey().equals(EPOS_CUSTOMER_ID))
                .findFirst()
                .map(p -> p.getValue())
                .orElse(null);
    }

    @Transient
    public boolean isSubscribedThisYear() {
        try {
            return nonNull(thisYearsSubscription());
        } catch (NoSuchElementException ignored) {
            return false;
        }
    }

    @Transient
    public boolean isPaidThisYear() {
        try {
            var subs = thisYearsSubscription();
            var payments = subs.getOrder().getPayment();
            return (!payments.isEmpty()) && payments.stream().allMatch(Payment::getCollected);
        } catch (NoSuchElementException ignored) {
            return false;
        }
    }

    @Transient
    public MemberSubscription getMostRecentSubscription() {
        if (nonNull(subscription)) {
            return subscription.stream()
                    .sorted(Comparator.comparing(MemberSubscription::getYear).reversed())
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Transient
    public boolean registeredInYear(int year) {
        if (nonNull(subscription)) {
            return subscription.stream().anyMatch(s -> s.getYear() == year);
        }
        return false;
    }

    @Transient
    public String getFullName() {
        return format(
                "{0} {1}",
                getAttributeMap().get("given-name").asText(),
                getAttributeMap().get("family-name").asText());
    }

    @Transient
    public String getGivenName() {
        return getAttributeMap().get("given-name").asText();
    }

    @Transient
    public String getFamilyName() {
        return getAttributeMap().get("family-name").asText();
    }

    @Transient
    public long getSubscriptionEpochSecond() {
        return getMostRecentSubscription()
                .getAddedDate()
                .toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.UTC);
    }

    private MemberSubscription thisYearsSubscription() {
        var now = LocalDate.now().getYear();
        return subscription.stream().filter(s -> s.getYear() == now).findFirst().orElseThrow();
    }

    public Member updateAttributesFrom(Member member) {
        var attributeSet =
                new TreeSet<MemberAttribute>(
                        (o1, o2) -> {
                            if (Objects.equals(o1.getMemberId(), o2.getMemberId())) {
                                if (isNull(o1.getDefinition())) return -1;
                                if (isNull(o2.getDefinition())) return 1;
                                return o1.getDefinition()
                                        .getKey()
                                        .compareTo(o2.getDefinition().getKey());
                            }
                            return o1.getMemberId().compareTo(o2.getMemberId());
                        });
        if (nonNull(member.getAttributes())) attributeSet.addAll(member.getAttributes());
        if (nonNull(this.attributes)) attributeSet.addAll(this.attributes);
        this.attributes = new ArrayList<>(attributeSet);
        return this;
    }
}
