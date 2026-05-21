package cricket.merstham.graphql.services.hooks.order;

import cricket.merstham.graphql.entity.OrderEntity;
import cricket.merstham.graphql.services.EmailService;
import cricket.merstham.graphql.services.hooks.Hook;
import cricket.merstham.shared.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

import static cricket.merstham.graphql.services.EmailService.MailTemplate.INCLUSIVE_KIT_ORDER;

@Service
public class InclusiveJuniorKitHook implements Hook<OrderEntity> {

    private final EmailService emailService;
    private final boolean enabled;

    @Autowired
    public InclusiveJuniorKitHook(
            EmailService emailService,
            @Value("${configuration.mail.inclusive-kit-email-enabled}") boolean enabled) {
        this.emailService = emailService;
        this.enabled = enabled;
    }

    @Override
    public void onConfirm(OrderEntity order, String paymentType, User user) {
        if (enabled) {
            var inclusiveKitSubscriptions =
                    order.getMemberSubscription().stream()
                            .filter(s -> s.getPricelistItem().getInclusiveKit())
                            .toList();
            if (!inclusiveKitSubscriptions.isEmpty()) {
                emailService.sendEmail(
                        user.getEmail(),
                        INCLUSIVE_KIT_ORDER,
                        Map.of("user", user, "subscriptions", inclusiveKitSubscriptions));
            }
        }
    }
}
