package cricket.merstham.graphql.services.hooks.order;

import cricket.merstham.graphql.entity.OrderEntity;
import cricket.merstham.graphql.services.SafeGuardingService;
import cricket.merstham.graphql.services.hooks.Hook;
import cricket.merstham.shared.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SafeguardingHook implements Hook<OrderEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(SafeguardingHook.class);

    private final SafeGuardingService safeGuardingService;

    @Autowired
    public SafeguardingHook(SafeGuardingService safeGuardingService) {
        this.safeGuardingService = safeGuardingService;
    }

    @Override
    public void onConfirm(OrderEntity data, String paymentType, User user) {
        try {
            data.getMemberSubscription().forEach(safeGuardingService::sendToQueue);
        } catch (Exception e) {
            LOG.error("Error sending member subscription to Safeguarding queue", e);
        }
    }
}
