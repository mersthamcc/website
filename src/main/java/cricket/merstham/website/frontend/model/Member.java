package cricket.merstham.website.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class Member {

    @JsonProperty
    private int id;
    @JsonProperty
    private int ownerUserId;
    @JsonProperty
    private LocalDate registrationDate;
    @JsonProperty
    private List<Subscription> subscription;
    @JsonProperty
    private List<Attribute> attributes;

    public int getId() {
        return id;
    }

    public int getOwnerUserId() {
        return ownerUserId;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public List<Subscription> getSubscription() {
        return subscription;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public Map<String, Object> getData() {
        return attributes.stream().collect(Collectors.toMap(
                a -> a.getDefinition().getKey(),
                a -> a.getValue()
        ));
    }
}
