package cricket.merstham.graphql.services.hooks.pass;

import cricket.merstham.graphql.entity.OrderEntity;
import cricket.merstham.graphql.services.PasskitUpdateService;
import cricket.merstham.graphql.services.hooks.Hook;
import cricket.merstham.shared.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplePassHook implements Hook<OrderEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(ApplePassHook.class);

    private final PasskitUpdateService passkitUpdateService;

    @Autowired
    public ApplePassHook(PasskitUpdateService passkitUpdateService) {
        this.passkitUpdateService = passkitUpdateService;
    }

    @Override
    public void onConfirm(OrderEntity data, String paymentType, User user) {
        LOG.info("Begin ApplePassHook onConfirm hook for order {}", data.getId());
        data.getMemberSubscription()
                .forEach(
                        subscription ->
                                passkitUpdateService.sendUpdatesForMember(
                                        subscription.getMember()));
        LOG.info("End ApplePassHook onConfirm hook for order {}", data.getId());
    }
}
