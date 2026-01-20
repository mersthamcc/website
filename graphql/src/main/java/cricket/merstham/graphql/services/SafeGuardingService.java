package cricket.merstham.graphql.services;

import cricket.merstham.graphql.entity.MemberEntity;
import cricket.merstham.graphql.entity.MemberSubscriptionEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.Objects.nonNull;

@Service
public class SafeGuardingService {
    private static final Logger LOG = LogManager.getLogger(SafeGuardingService.class);

    private final SqsService sqsService;

    @Autowired
    public SafeGuardingService(SqsService sqsService) {
        this.sqsService = sqsService;
    }

    public void sendToQueue(MemberSubscriptionEntity subscription) {
        LOG.info("Syncing member {} to SafeGuarding", subscription.getMember().getId());
        sqsService.sendSafeguardingSubject(createRequest(subscription));
    }

    private Map<String, Object> createRequest(MemberSubscriptionEntity subscription) {
        Map<String, Object> result = new HashMap<>();
        result.putAll(
                Map.of(
                        "id",
                        subscription.getMember().getId(),
                        "foreName",
                        subscription.getMember().getStringAttribute("given-name"),
                        "surname",
                        subscription.getMember().getStringAttribute("family-name"),
                        "gender",
                        subscription.getMember().getStringAttribute("gender"),
                        "category",
                        subscription.getPricelistItem().getMemberCategory().getKey(),
                        "admissionDate",
                        subscription.getMember().getRegistrationDate()));
        var dob = determineEmail(subscription.getMember());
        if (nonNull(dob)) {
            result.put("dob", dob);
        }
        var email = determineEmail(subscription.getMember());
        if (nonNull(email)) {
            result.put("emailAddress", email);
        }
        var medicalConditions = determineMedicalCondition(subscription.getMember());
        if (nonNull(medicalConditions) && !medicalConditions.isEmpty()) {
            result.put("medicalNote", medicalConditions);
        }
        return result;
    }

    private String determineDob(MemberEntity member) {
        try {
            return member.getStringAttribute("dob");
        } catch (NoSuchElementException ignored) {
            return null;
        }
    }

    private String determineEmail(MemberEntity member) {
        try {
            return member.getStringAttribute("email");
        } catch (NoSuchElementException ignored) {
            return null;
        }
    }

    private String determineMedicalCondition(MemberEntity member) {
        try {
            return member.getStringAttribute("medical-conditions");
        } catch (NoSuchElementException ignored) {
            return null;
        }
    }
}
