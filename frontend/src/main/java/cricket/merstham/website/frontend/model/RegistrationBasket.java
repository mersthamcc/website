package cricket.merstham.website.frontend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cricket.merstham.shared.dto.MemberSubscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@JsonSerialize
@Accessors(chain = true)
public class RegistrationBasket implements Serializable {

    private static final long serialVersionUID = 20210522173000L;

    @JsonProperty private String id;

    @JsonProperty private Map<UUID, MemberSubscription> subscriptions;

    public RegistrationBasket() {
        this.id = UUID.randomUUID().toString();
        this.subscriptions = new HashMap<>();
    }

    public RegistrationBasket putSubscription(UUID key, MemberSubscription subscription) {
        this.subscriptions.put(key, subscription);
        return this;
    }

    public MemberSubscription getSubscription(UUID key) {
        return this.subscriptions.get(key);
    }

    public BigDecimal getBasketTotal() {
        return subscriptions.values().stream()
                .map(MemberSubscription::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
