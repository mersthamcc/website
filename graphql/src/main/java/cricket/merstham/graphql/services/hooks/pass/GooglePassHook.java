package cricket.merstham.graphql.services.hooks.pass;

import cricket.merstham.graphql.entity.OrderEntity;
import cricket.merstham.graphql.services.PassGeneratorService;
import cricket.merstham.graphql.services.hooks.Hook;
import cricket.merstham.shared.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static cricket.merstham.shared.IdentifierConstants.GOOGLE_PASS_SERIAL;

@Service
public class GooglePassHook implements Hook<OrderEntity> {

    private static Logger LOG = LoggerFactory.getLogger(GooglePassHook.class);

    private final PassGeneratorService passGeneratorService;

    @Autowired
    public GooglePassHook(PassGeneratorService passGeneratorService) {
        this.passGeneratorService = passGeneratorService;
    }

    @Override
    public void onConfirm(OrderEntity order, String paymentType, User user) {
        try {
            order.getMemberSubscription()
                    .forEach(
                            subscription -> {
                                var member = subscription.getMember();
                                if (member.getIdentifiers().containsKey(GOOGLE_PASS_SERIAL)) {
                                    passGeneratorService.createOrUpdateGooglePassObject(
                                            member,
                                            member.getIdentifiers().get(GOOGLE_PASS_SERIAL));
                                }
                            });
        } catch (Exception e) {
            LOG.error("Error updating Google Wallet Pass", e);
        }
    }
}
