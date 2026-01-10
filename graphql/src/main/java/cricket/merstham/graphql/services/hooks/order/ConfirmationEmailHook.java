package cricket.merstham.graphql.services.hooks.order;

import cricket.merstham.graphql.entity.OrderEntity;
import cricket.merstham.graphql.services.EmailService;
import cricket.merstham.graphql.services.hooks.Hook;
import cricket.merstham.shared.dto.Order;
import cricket.merstham.shared.dto.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static cricket.merstham.graphql.services.EmailService.MailTemplate.MEMBERSHIP_CONFIRM;

@Service
public class ConfirmationEmailHook implements Hook<OrderEntity> {

    private final EmailService emailService;
    private final ModelMapper modelMapper;

    @Autowired
    public ConfirmationEmailHook(EmailService emailService, ModelMapper modelMapper) {
        this.emailService = emailService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void onConfirm(OrderEntity order, String paymentType, User user) {
        emailService.sendEmail(
                user.getEmail(),
                MEMBERSHIP_CONFIRM,
                Map.of(
                        "order", modelMapper.map(order, Order.class),
                        "user", user,
                        "paymentType", paymentType));
    }
}
