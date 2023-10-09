package cricket.merstham.website.frontend.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cricket.merstham.shared.utils.IdGenerator;
import org.springframework.session.Session;

import java.beans.Transient;
import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@JsonSerialize
public class DynamoSession implements Session, Serializable {

    @Serial private static final long serialVersionUID = 1849620608141267837L;

    @JsonProperty private String id;
    @JsonProperty private Instant creationTime;
    @JsonProperty private Instant lastAccessedTime;
    @JsonProperty private Instant expireAt;
    @JsonProperty private long maxInactiveIntervalSeconds;
    @JsonProperty private Map<String, Object> attributes;

    public DynamoSession(String id, long maxInactiveIntervalSeconds) {
        this.id = id;
        this.creationTime = Instant.now();
        this.lastAccessedTime = this.creationTime;
        this.maxInactiveIntervalSeconds = maxInactiveIntervalSeconds;
        this.attributes = new HashMap<>();
    }

    @Override
    public String changeSessionId() {
        id = IdGenerator.generate();
        return id;
    }

    @Override
    public Instant getCreationTime() {
        return creationTime;
    }

    @Override
    public void setLastAccessedTime(Instant lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
        expireAt = lastAccessedTime.plusSeconds(maxInactiveIntervalSeconds);
    }

    @Override
    public Instant getLastAccessedTime() {
        return lastAccessedTime;
    }

    @Override
    public void setMaxInactiveInterval(Duration interval) {
        maxInactiveIntervalSeconds = interval.getSeconds();
    }

    @Override
    public Duration getMaxInactiveInterval() {
        return Duration.ofSeconds(maxInactiveIntervalSeconds);
    }

    @Override
    public boolean isExpired() {
        return maxInactiveIntervalSeconds >= 0 && Instant.now().isAfter(expireAt);
    }

    @Override
    public String getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getAttribute(String attributeName) {
        return (T) attributes.get(attributeName);
    }

    @Override
    @Transient
    public Set<String> getAttributeNames() {
        return new HashSet<>(attributes.keySet());
    }

    @Override
    public void setAttribute(String attributeName, Object attributeValue) {
        if (attributeValue == null) {
            removeAttribute(attributeName);
        } else {
            attributes.put(attributeName, attributeValue);
        }
    }

    @Override
    public void removeAttribute(String attributeName) {
        attributes.remove(attributeName);
    }
}
