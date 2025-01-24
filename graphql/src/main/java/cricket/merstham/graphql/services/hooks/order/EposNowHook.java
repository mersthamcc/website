package cricket.merstham.graphql.services.hooks.order;

import cricket.merstham.graphql.entity.OrderEntity;
import cricket.merstham.graphql.services.EposNowService;
import cricket.merstham.graphql.services.hooks.Hook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EposNowHook implements Hook<OrderEntity> {

    private final EposNowService eposNowService;

    @Autowired
    public EposNowHook(EposNowService eposNowService) {
        this.eposNowService = eposNowService;
    }

    @Override
    public void onConfirm(OrderEntity data) {
        data.getMemberSubscription()
                .forEach(member -> eposNowService.sendToQueue(member.getMember()));
    }
}
