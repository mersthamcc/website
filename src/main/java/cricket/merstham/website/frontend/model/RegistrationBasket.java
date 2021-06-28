package cricket.merstham.website.frontend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@JsonSerialize
public class RegistrationBasket implements Serializable {

    private static final long serialVersionUID = 20210522173000L;

    @JsonProperty private String id;

    @JsonProperty private Map<UUID, Subscription> subscriptions;

    public RegistrationBasket() {
        this.id = UUID.randomUUID().toString();
        this.subscriptions = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public RegistrationBasket setId(String id) {
        this.id = id;
        return this;
    }

    public Map<UUID, Subscription> getSubscriptions() {
        return subscriptions;
    }

    public RegistrationBasket setSubscriptions(Map<UUID, Subscription> subscriptions) {
        this.subscriptions = subscriptions;
        return this;
    }

    public RegistrationBasket addSubscription(Subscription subscription) {
        this.subscriptions.put(subscription.getUuid(), subscription);
        return this;
    }

    public Subscription updateSubscription(Subscription subscription) {
        return this.subscriptions.get(subscription.getUuid()).updateFrom(subscription);
    }

    public BigDecimal getBasketTotal() {
        return subscriptions.values().stream()
                .map(Subscription::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public RegistrationBasket removeSuscription(UUID uuid) {
        subscriptions.remove(uuid);
        return this;
    }

    public void reset() {
        this.id = UUID.randomUUID().toString();
        this.subscriptions = new HashMap<>();
    }
}
