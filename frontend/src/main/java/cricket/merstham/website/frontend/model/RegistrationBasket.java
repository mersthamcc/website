package cricket.merstham.website.frontend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cricket.merstham.shared.dto.Member;
import cricket.merstham.shared.dto.MemberSubscription;
import cricket.merstham.shared.dto.RegistrationAction;
import cricket.merstham.website.frontend.model.discounts.Discount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@JsonSerialize
@Accessors(chain = true)
public class RegistrationBasket implements Serializable {

    @Serial private static final long serialVersionUID = 20210522173000L;
    private static final List<RegistrationAction> CHARGEABLE_ACTIONS =
            List.of(RegistrationAction.NEW, RegistrationAction.RENEW);

    @JsonProperty private String id;

    @JsonProperty private Map<UUID, MemberSubscription> subscriptions;

    @JsonProperty private List<Discount> activeDiscounts;

    public RegistrationBasket(List<Discount> activeDiscounts) {
        this.id = UUID.randomUUID().toString();
        this.subscriptions = new HashMap<>();
        this.activeDiscounts = activeDiscounts;
    }

    public RegistrationBasket putSubscription(UUID key, MemberSubscription subscription) {
        this.subscriptions.put(key, subscription);
        return this;
    }

    public MemberSubscription getSubscription(UUID key) {
        return this.subscriptions.get(key);
    }

    public Map<String, BigDecimal> getDiscounts() {
        return activeDiscounts.stream()
                .map(discount -> Map.entry(discount.getDiscountName(), discount.apply(this)))
                .filter(discount -> discount.getValue().doubleValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public List<MemberSubscription> getChargeableSubscriptions() {
        return subscriptions.values().stream()
                .filter(s -> CHARGEABLE_ACTIONS.contains(s.getAction()))
                .toList();
    }

    public BigDecimal getItemTotal() {
        return getChargeableSubscriptions().stream()
                .map(MemberSubscription::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getBasketTotal() {
        return getChargeableSubscriptions().stream()
                .map(MemberSubscription::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .subtract(
                        getDiscounts().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public RegistrationBasket removeSubscription(UUID key) {
        subscriptions.remove(key);
        return this;
    }

    public void reset() {
        this.id = UUID.randomUUID().toString();
        this.subscriptions = new HashMap<>();
    }

    public void addExistingMembers(List<Member> members) {
        members.forEach(
                member -> {
                    var uuid = UUID.randomUUID();
                    if (!subscriptions.containsKey(uuid))
                        subscriptions.put(
                                uuid,
                                MemberSubscription.builder()
                                        .action(RegistrationAction.NONE)
                                        .year(member.getMostRecentSubscription().getYear())
                                        .category(
                                                member.getMostRecentSubscription()
                                                        .getPriceListItem()
                                                        .getMemberCategory()
                                                        .getKey())
                                        .member(member)
                                        .build());
                });
    }
}
