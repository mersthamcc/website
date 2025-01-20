package cricket.merstham.graphql.services;

import cricket.merstham.graphql.entity.MemberEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static cricket.merstham.shared.IdentifierConstants.EPOS_CUSTOMER_ID;
import static java.util.Objects.nonNull;

@Service
public class EposNowService {
    private static final Logger LOG = LogManager.getLogger(EposNowService.class);

    private final SqsService sqsService;

    @Autowired
    public EposNowService(SqsService sqsService) {
        this.sqsService = sqsService;
    }

    public void sendToQueue(MemberEntity member) {
        LOG.info("Syncing member {} to EposNow", member.getId());
        sqsService.sendCustomer(createRequest(member));
    }

    private Map<String, Object> createRequest(MemberEntity member) {
        Map<String, Object> result = new HashMap<>();
        result.putAll(
                Map.of(
                        "id",
                        member.getId(),
                        "firstName",
                        member.getStringAttribute("given-name"),
                        "lastName",
                        member.getStringAttribute("family-name"),
                        "category",
                        member.getMostRecentSubscription()
                                .getPricelistItem()
                                .getMemberCategory()
                                .getKey(),
                        "registrationDate",
                        member.getRegistrationDate(),
                        "reference",
                        member.getMostRecentSubscription().getOrder().getUuid()));
        var email = determineEmail(member);
        if (nonNull(email)) {
            result.put("emailAddress", determineEmail(member));
        }
        if (nonNull(member.getIdentifiers().get(EPOS_CUSTOMER_ID))) {
            result.put("eposCustomerId", member.getIdentifiers().get(EPOS_CUSTOMER_ID));
        }
        return result;
    }

    private String determineEmail(MemberEntity member) {
        try {
            return member.getStringAttribute("email");
        } catch (NoSuchElementException ignored) {
        }

        try {
            return member.getStringAttribute("parent-email-1");
        } catch (NoSuchElementException ignored) {
            return null;
        }
    }
}
