package cricket.merstham.website.frontend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cricket.merstham.shared.dto.MemberSubscription;
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

    public BigDecimal getBasketTotal() {
        return subscriptions.values().stream()
                .map(MemberSubscription::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .subtract(
                        getDiscounts().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public RegistrationBasket removeSuscription(UUID key) {
        subscriptions.remove(key);
        return this;
    }

    public void reset() {
        this.id = UUID.randomUUID().toString();
        this.subscriptions = new HashMap<>();
    }
}
