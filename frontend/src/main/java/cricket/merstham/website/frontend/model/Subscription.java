package cricket.merstham.website.frontend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cricket.merstham.shared.dto.MemberCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static cricket.merstham.website.frontend.helpers.AttributeConverter.convert;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@RedisHash
public class Subscription implements Serializable {

    private static final long serialVersionUID = 20210522173123L;

    @JsonProperty private UUID uuid;

    @JsonProperty private String category;

    @JsonProperty private int pricelistItemId;

    @JsonProperty private Map<String, Object> member;

    @JsonProperty private Map<String, AttributeDefinition> attributes;

    @JsonProperty private BigDecimal price;

    @JsonProperty private RegistrationAction action;

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

    public Subscription updateDefinition(MemberCategory category) {
        Map<String, AttributeDefinition> attrs = new HashMap<>();
        for (var section : category.getForm()) {
            for (var attr : section.getSection().getAttribute()) {
                attrs.put(
                        attr.getDefinition().getKey(),
                        (new AttributeDefinition())
                                .setSection(section.getSection().getKey())
                                .setMandatory(attr.getMandatory())
                                .setType(attr.getDefinition().getType().name())
                                .setChoices(attr.getDefinition().getChoices()));
            }
        }
        return this.setCategory(category.getKey()).setAttributes(attrs);
    }
}
