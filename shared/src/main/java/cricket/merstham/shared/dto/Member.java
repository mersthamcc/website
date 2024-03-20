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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static cricket.merstham.shared.IdentifierConstants.EPOS_CUSTOMER_ID;
import static cricket.merstham.shared.IdentifierConstants.PLAYER_ID;
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
            var subscription = thisYearsSubscription();
            var payments = subscription.getOrder().getPayment();
            return (!payments.isEmpty()) && payments.stream().allMatch(Payment::getCollected);
        } catch (NoSuchElementException ignored) {
            return false;
        }
    }

    private MemberSubscription thisYearsSubscription() {
        var now = LocalDate.now().getYear();
        return subscription.stream().filter(s -> s.getYear() == now).findFirst().orElseThrow();
    }
}
