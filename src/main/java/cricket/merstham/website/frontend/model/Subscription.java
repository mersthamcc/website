package cricket.merstham.website.frontend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cricket.merstham.website.graph.MembershipCategoriesQuery;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import static cricket.merstham.website.frontend.helpers.AttributeConverter.convert;

@JsonSerialize
@RedisHash
public class Subscription implements Serializable {

    private static final long serialVersionUID = 20210522173123L;

    @JsonProperty private UUID uuid;

    @JsonProperty private String category;

    @JsonProperty private int pricelistItemId = 0;

    @JsonProperty private Map<String, Object> member;

    @JsonProperty private Map<String, AttributeDefinition> attributes = new HashMap<>();

    @JsonProperty private BigDecimal price;

    @JsonProperty private RegistrationAction action = RegistrationAction.NEW;

    public UUID getUuid() {
        return uuid;
    }

    public Subscription setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public Subscription setCategory(String category) {
        this.category = category;
        return this;
    }

    public int getPricelistItemId() {
        return pricelistItemId;
    }

    public Subscription setPricelistItemId(int pricelistItemId) {
        this.pricelistItemId = pricelistItemId;
        return this;
    }

    public Map<String, Object> getMember() {
        return member;
    }

    public Subscription setMember(Map<String, Object> member) {
        this.member = member;
        return this;
    }

    public Map<String, AttributeDefinition> getAttributes() {
        return attributes;
    }

    public Subscription setAttributes(Map<String, AttributeDefinition> attributes) {
        this.attributes = attributes;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Subscription setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public RegistrationAction getAction() {
        return action;
    }

    public Subscription setAction(RegistrationAction action) {
        this.action = action;
        return this;
    }

    public Subscription updateFrom(Subscription subscription) {
        if (subscription.member != null) {
            subscription.member.forEach(
                    (key, value) -> {
                        Object convertedValue = convert(attributes, key, value);
                        member.put(key, convertedValue);
                    });
        }
        if (subscription.getPrice() != null) this.setPrice(subscription.getPrice());
        if (subscription.getCategory() != null) this.setCategory(subscription.getCategory());
        if (subscription.getAttributes() != null) this.setAttributes(subscription.getAttributes());
        if (subscription.getPricelistItemId() > 0)
            this.setPricelistItemId(subscription.getPricelistItemId());
        return this;
    }

    public Subscription updateDefinition(MembershipCategoriesQuery.MembershipCategory category) {
        Map<String, AttributeDefinition> attrs = new HashMap<>();
        for (var section : category.form()) {
            for (var attr : section.section().attribute()) {
                attrs.put(
                        attr.definition().key(),
                        (new AttributeDefinition())
                                .setSection(section.section().key())
                                .setMandatory(attr.mandatory())
                                .setType(attr.definition().type().rawValue())
                                .setChoices((List<String>) attr.definition().choices()));
            }
        }
        return this.setCategory(category.key()).setAttributes(attrs);
    }
}
