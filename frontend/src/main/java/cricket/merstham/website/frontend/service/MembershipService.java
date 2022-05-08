package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import cricket.merstham.website.frontend.model.AttributeDefinition;
import cricket.merstham.website.frontend.model.Order;
import cricket.merstham.website.frontend.model.RegistrationBasket;
import cricket.merstham.website.frontend.model.admintables.Member;
import cricket.merstham.website.graph.AddPaymentToOrderMutation;
import cricket.merstham.website.graph.AttributesQuery;
import cricket.merstham.website.graph.CreateMemberMutation;
import cricket.merstham.website.graph.CreateOrderMutation;
import cricket.merstham.website.graph.MemberQuery;
import cricket.merstham.website.graph.MembersQuery;
import cricket.merstham.website.graph.MembershipCategoriesQuery;
import cricket.merstham.website.graph.UpdateMemberMutation;
import cricket.merstham.website.graph.type.AttributeInput;
import cricket.merstham.website.graph.type.MemberInput;
import cricket.merstham.website.graph.type.MemberSubscriptionInput;
import cricket.merstham.website.graph.type.PaymentInput;
import cricket.merstham.website.graph.type.StringFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static cricket.merstham.website.frontend.configuration.CacheConfiguration.MEMBER_SUMMARY_CACHE;
import static java.text.MessageFormat.format;

@Service
public class MembershipService {
    private static final Logger LOG = LoggerFactory.getLogger(MembershipService.class);

    private GraphService graphService;

    @Autowired
    public MembershipService(GraphService graphService) {
        this.graphService = graphService;
    }

    public Order registerMembersFromBasket(
            RegistrationBasket basket, OAuth2AccessToken accessToken) {
        var createOrder = new CreateOrderMutation(basket.getId());
        var order = new Order();
        try {
            Response<CreateOrderMutation.Data> orderResult =
                    graphService.executeMutation(createOrder, accessToken);
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
                var result = graphService.executeMutation(createMemberMutation, accessToken);
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
            OAuth2AccessToken accessToken) {
        var addPaymentToOrderMutation =
                new AddPaymentToOrderMutation(
                        order.getId(),
                        PaymentInput.builder()
                                .type(type)
                                .reference(reference)
                                .date(date.toLocalDate())
                                .amount(amount.doubleValue())
                                .processingFees(fees.doubleValue())
                                .collected(collected)
                                .reconciled(reconciled)
                                .build());
        try {
            Response<AddPaymentToOrderMutation.Data> result =
                    graphService.executeMutation(addPaymentToOrderMutation, accessToken);
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

    public List<MembershipCategoriesQuery.MembershipCategory> getMembershipCategories() {
        var query = new MembershipCategoriesQuery(StringFilter.builder().build());
        try {
            Response<MembershipCategoriesQuery.Data> result = graphService.executeQuery(query);
            return result.getData().membershipCategories();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MembershipCategoriesQuery.MembershipCategory getMembershipCategory(String categoryName) {
        var query =
                new MembershipCategoriesQuery(StringFilter.builder().equals(categoryName).build());
        try {
            Response<MembershipCategoriesQuery.Data> result = graphService.executeQuery(query);
            return result.getData().membershipCategories().get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MembersQuery.Member> getAllMembers(OAuth2AccessToken accessToken) {
        var query = new MembersQuery();
        try {
            Response<MembersQuery.Data> result = graphService.executeQuery(query, accessToken);
            return result.getData().members();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Cacheable(value = MEMBER_SUMMARY_CACHE, key = "#accessToken.tokenValue")
    public List<Member> getMemberSummary(OAuth2AccessToken accessToken) {
        return getAllMembers(accessToken).stream()
                .map(
                        m ->
                                Member.builder()
                                        .id(Integer.toString(m.id()))
                                        .familyName(
                                                getMemberAttributeString(
                                                        m.attributes(), "family-name", ""))
                                        .givenName(
                                                getMemberAttributeString(
                                                        m.attributes(), "given-name", ""))
                                        .category(
                                                m.subscription().stream()
                                                        .findFirst()
                                                        .map(s -> s.pricelistItem().description())
                                                        .orElse("unknown"))
                                        .lastSubscription(
                                                m.subscription().stream()
                                                        .findFirst()
                                                        .map(s -> Integer.toString(s.year()))
                                                        .orElse("unknown"))
                                        .editLink(
                                                URI.create(
                                                        format(
                                                                "/administration/membership/edit/{0}",
                                                                m.id())))
                                        .build())
                .collect(Collectors.toList());
    }

    public Optional<MemberQuery.Member> get(int id, OAuth2AccessToken accessToken) {
        var query = new MemberQuery(id);
        try {
            Response<MemberQuery.Data> result = graphService.executeQuery(query, accessToken);

            return Optional.of(result.getData().member());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public UpdateMemberMutation.UpdateMember update(
            int id, OAuth2AccessToken accessToken, Map<String, Object> data) {
        var request =
                new UpdateMemberMutation(
                        id,
                        data.entrySet().stream()
                                .map(
                                        f ->
                                                AttributeInput.builder()
                                                        .key(f.getKey())
                                                        .value(f.getValue())
                                                        .build())
                                .collect(Collectors.toList()));
        try {
            Response<UpdateMemberMutation.Data> result =
                    graphService.executeMutation(request, accessToken);

            return result.getData().updateMember();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, AttributeDefinition> getAttributes() {
        var query = new AttributesQuery();
        try {
            Response<AttributesQuery.Data> result = graphService.executeQuery(query);

            return result.getData().attributes().stream()
                    .collect(
                            Collectors.toMap(
                                    a -> a.key(),
                                    a ->
                                            new AttributeDefinition()
                                                    .setKey(a.key())
                                                    .setType(a.type().rawValue())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getMemberAttributeString(
            List<MembersQuery.Attribute> attributeList, String field, String defaultValue) {
        return attributeList.stream()
                .filter(a -> a.definition().key().equals(field))
                .findFirst()
                .map(f -> (String) f.value())
                .orElse(defaultValue);
    }
}
