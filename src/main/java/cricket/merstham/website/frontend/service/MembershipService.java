package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import cricket.merstham.website.frontend.model.Order;
import cricket.merstham.website.frontend.model.RegistrationBasket;
import cricket.merstham.website.graph.AddPaymentToOrderMutation;
import cricket.merstham.website.graph.CreateMemberMutation;
import cricket.merstham.website.graph.CreateOrderMutation;
import cricket.merstham.website.graph.type.AttributeInput;
import cricket.merstham.website.graph.type.MemberInput;
import cricket.merstham.website.graph.type.MemberSubscriptionInput;
import cricket.merstham.website.graph.type.PaymentInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MembershipService {
    private static final Logger LOG = LoggerFactory.getLogger(MembershipService.class);

    private GraphService graphService;

    @Autowired
    public MembershipService(GraphService graphService) {
        this.graphService = graphService;
    }

    public Order registerMembersFromBasket(RegistrationBasket basket, Principal principal) {
        var createOrder = new CreateOrderMutation(basket.getId());
        var order = new Order();
        try {
            Response<CreateOrderMutation.Data> orderResult =
                    graphService.executeMutation(createOrder, principal);
            if (orderResult.hasErrors()) {
                throw new RuntimeException(
                        "GraphQL error(s) registering member: "
                                + String.join(
                                        "\n",
                                        orderResult.getErrors().stream()
                                                .map(error -> error.getMessage())
                                                .collect(Collectors.toList())));
            }
            order.setId(orderResult.getData().createOrder().id())
                    .setUuid(UUID.fromString(orderResult.getData().createOrder().uuid()))
                    .setTotal(basket.getBasketTotal())
                    .setSubscriptions(basket.getSubscriptions());
        } catch (IOException e) {
            LOG.error("Error creating order", e);
            throw new RuntimeException("Error creating order", e);
        }

        for (var subscription : basket.getSubscriptions().entrySet()) {
            var memberInput =
                    MemberInput.builder()
                            .attributes(
                                    subscription.getValue().getMember().entrySet().stream()
                                            .map(
                                                    a ->
                                                            AttributeInput.builder()
                                                                    .key(a.getKey())
                                                                    .value(a.getValue())
                                                                    .build())
                                            .collect(Collectors.toList()))
                            .subscription(
                                    MemberSubscriptionInput.builder()
                                            .year(LocalDate.now().getYear())
                                            .pricelistItemId(
                                                    subscription.getValue().getPricelistItemId())
                                            .price(subscription.getValue().getPrice().doubleValue())
                                            .orderId(order.getId())
                                            .build())
                            .build();

            var createMemberMutation = new CreateMemberMutation(memberInput);
            try {
                var result = graphService.executeMutation(createMemberMutation, principal);
                if (result.hasErrors()) {
                    throw new RuntimeException(
                            "GraphQL error(s) registering member: "
                                    + result.getErrors().stream()
                                            .map(Error::getMessage)
                                            .collect(Collectors.joining("\n")));
                }
            } catch (IOException e) {
                LOG.error("Error registering member", e);
                throw new RuntimeException("Error registering member", e);
            }
        }
        return order;
    }

    public AddPaymentToOrderMutation.AddPaymentToOrder createPayment(
            Order order,
            String type,
            String reference,
            LocalDateTime date,
            BigDecimal amount,
            BigDecimal fees,
            boolean collected,
            boolean reconciled,
            Principal principal) {
        var addPaymentToOrderMutation =
                new AddPaymentToOrderMutation(
                        order.getId(),
                        PaymentInput.builder()
                                .type(type)
                                .reference(reference)
                                .date(date)
                                .amount(amount.doubleValue())
                                .processingFees(fees.doubleValue())
                                .collected(collected)
                                .reconciled(reconciled)
                                .build());
        try {
            Response<AddPaymentToOrderMutation.Data> result =
                    graphService.executeMutation(addPaymentToOrderMutation, principal);
            if (result.hasErrors()) {
                throw new RuntimeException(
                        "GraphQL error(s) creating payment: "
                                + result.getErrors().stream()
                                        .map(Error::getMessage)
                                        .collect(Collectors.joining("\n")));
            }
            return result.getData().addPaymentToOrder();
        } catch (IOException e) {
            LOG.error("Error registering payment", e);
            throw new RuntimeException("Error registering payment", e);
        }
    }
}
