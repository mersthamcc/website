package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

/** DTO for {@link cricket.merstham.graphql.entity.ClubDrawSubscriptionEntity} */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClubDrawSubscription implements Serializable {
    @Serial private static final long serialVersionUID = -4094263647877914009L;

    @JsonProperty private Integer id;
    @JsonProperty private String ownerUserId;
    @JsonProperty private String subscriptionId;
    @JsonProperty private Instant createDate;
    @JsonProperty private Instant lastUpdated;
    @JsonProperty private Boolean active;
    @JsonProperty private Integer noOfTickets;
    @JsonProperty private Set<ClubDrawPayment> clubDrawPayments = new LinkedHashSet<>();
    @JsonProperty private Set<ClubDrawWinner> clubDrawWinners = new LinkedHashSet<>();
}
