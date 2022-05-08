package cricket.merstham.graphql.inputs;

import org.springframework.data.web.ProjectedPayload;

import java.time.Instant;
import java.util.List;

@ProjectedPayload
public interface MemberInput {
    Instant getRegistrationDate();

    List<AttributeInput> getAttributes();

    MemberSubscriptionInput getSubscription();
}
