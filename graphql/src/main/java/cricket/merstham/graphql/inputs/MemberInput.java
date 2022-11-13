package cricket.merstham.graphql.inputs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@JsonSerialize
@Getter
@Builder
public class MemberInput {

    @JsonProperty private Instant registrationDate;

    @JsonProperty private List<AttributeInput> attributes;

    @JsonProperty private MemberSubscriptionInput subscription;
}
