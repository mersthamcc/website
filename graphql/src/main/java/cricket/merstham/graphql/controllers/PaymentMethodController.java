package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.PaymentMethodService;
import cricket.merstham.shared.dto.UserPaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

import static cricket.merstham.graphql.helpers.UserHelper.getSubject;

@Controller
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @Autowired
    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @QueryMapping
    public List<UserPaymentMethod> getPaymentMethods(@Argument("userId") String userId) {
        return paymentMethodService.getPaymentMethods(userId);
    }

    @QueryMapping
    public List<UserPaymentMethod> getMyMethods(Principal principal) {
        return paymentMethodService.getPaymentMethods(getSubject(principal));
    }

    @MutationMapping
    public UserPaymentMethod addPaymentMethod(
            @Argument("paymentMethod") UserPaymentMethod paymentMethod, Principal principal) {
        return paymentMethodService.savePaymentMethod(paymentMethod, principal);
    }
}
