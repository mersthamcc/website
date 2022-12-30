package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import cricket.merstham.shared.dto.AttributeDefinition;
import cricket.merstham.shared.dto.Member;
import cricket.merstham.shared.dto.MemberAttribute;
import cricket.merstham.shared.dto.MemberCategory;
import cricket.merstham.shared.dto.Order;
import cricket.merstham.shared.types.AttributeType;
import cricket.merstham.website.frontend.exception.GraphException;
import cricket.merstham.website.frontend.model.RegistrationBasket;
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
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cricket.merstham.website.frontend.configuration.CacheConfiguration.MEMBER_SUMMARY_CACHE;
import static cricket.merstham.website.frontend.helpers.AttributeConverter.convert;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@Service
public class MembershipService {
    private static final Logger LOG = LoggerFactory.getLogger(MembershipService.class);

    private final GraphService graphService;
    private final ModelMapper modelMapper;

    @Autowired
    public MembershipService(GraphService graphService, ModelMapper modelMapper) {
        this.graphService = graphService;
        this.modelMapper = modelMapper;
    }

    public Order registerMembersFromBasket(
            RegistrationBasket basket, OAuth2AccessToken accessToken, Locale locale) {
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
            order.setId(orderResult.getData().getCreateOrder().getId())
                    .setUuid(orderResult.getData().getCreateOrder().getUuid())
                    .setTotal(basket.getBasketTotal());
        } catch (IOException e) {
            LOG.error("Error creating order", e);
            throw new RuntimeException("Error creating order", e);
        }

        for (var subscription : basket.getSubscriptions().entrySet()) {
            var memberInput =
                    MemberInput.builder()
                            .attributes(
                                    subscription.getValue().getMember().getAttributes().stream()
                                            .map(
                                                    a ->
                                                            AttributeInput.builder()
                                                                    .key(a.getDefinition().getKey())
                                                                    .value(a.getValue())
                                                                    .build())
                                            .collect(Collectors.toList()))
                            .subscription(
                                    MemberSubscriptionInput.builder()
                                            .year(LocalDate.now().getYear())
                                            .priceListItemId(
                                                    subscription
                                                            .getValue()
                                                            .getPriceListItem()
                                                            .getId())
                                            .price(subscription.getValue().getPrice().doubleValue())
                                            .orderId(order.getId())
                                            .addedDate(LocalDate.now())
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
            return result.getData().getAddPaymentToOrder();
        } catch (IOException e) {
            LOG.error("Error registering payment", e);
            throw new RuntimeException("Error registering payment", e);
        }
    }

    public List<MemberCategory> getMembershipCategories() {
        var query = new MembershipCategoriesQuery(StringFilter.builder().build());
        try {
            Response<MembershipCategoriesQuery.Data> result = graphService.executeQuery(query);
            var categories = result.getData().getMembershipCategories();
            return categories.stream()
                    .map(c -> modelMapper.map(c, MemberCategory.class))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MemberCategory getMembershipCategory(String categoryName) {
        var query =
                new MembershipCategoriesQuery(StringFilter.builder().equals(categoryName).build());
        try {
            Response<MembershipCategoriesQuery.Data> result = graphService.executeQuery(query);
            return modelMapper.map(
                    result.getData().getMembershipCategories().get(0), MemberCategory.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Member> getAllMembers(OAuth2AccessToken accessToken) {
        var query = new MembersQuery();
        try {
            Response<MembersQuery.Data> result = graphService.executeQuery(query, accessToken);
            return result.getData().getMembers().stream()
                    .map(m -> modelMapper.map(m, Member.class))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Cacheable(value = MEMBER_SUMMARY_CACHE, key = "#accessToken.tokenValue")
    public List<cricket.merstham.website.frontend.model.admintables.Member> getMemberSummary(
            OAuth2AccessToken accessToken) {
        return getAllMembers(accessToken).stream()
                .map(
                        m ->
                                cricket.merstham.website.frontend.model.admintables.Member.builder()
                                        .id(Integer.toString(m.getId()))
                                        .familyName(
                                                getMemberAttributeString(
                                                        m.getAttributes(), "family-name", ""))
                                        .givenName(
                                                getMemberAttributeString(
                                                        m.getAttributes(), "given-name", ""))
                                        .category(
                                                m.getSubscription().stream()
                                                        .findFirst()
                                                        .map(
                                                                s ->
                                                                        s.getPriceListItem()
                                                                                .getDescription())
                                                        .orElse("unknown"))
                                        .lastSubscription(
                                                m.getSubscription().stream()
                                                        .findFirst()
                                                        .map(s -> Integer.toString(s.getYear()))
                                                        .orElse("unknown"))
                                        .build())
                .collect(Collectors.toList());
    }

    public Optional<Member> get(int id, OAuth2AccessToken accessToken) {
        var query = new MemberQuery(id);
        try {
            Response<MemberQuery.Data> result = graphService.executeQuery(query, accessToken);
            if (isNull(result.getData().getMember())) {
                return Optional.empty();
            }
            return Optional.of(
                    modelMapper.map(
                            result.getData().getMember(),
                            cricket.merstham.shared.dto.Member.class));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Member update(
            int id,
            OAuth2AccessToken accessToken,
            Locale locale,
            MultiValueMap<String, Object> data) {
        var attributes = getAttributes();
        var request =
                new UpdateMemberMutation(
                        id,
                        data.entrySet().stream()
                                .filter(a -> attributes.containsKey(a.getKey()))
                                .map(
                                        f ->
                                                AttributeInput.builder()
                                                        .key(f.getKey())
                                                        .value(
                                                                convert(
                                                                        attributes.get(f.getKey()),
                                                                        f.getValue()))
                                                        .build())
                                .collect(Collectors.toList()));
        try {
            Response<UpdateMemberMutation.Data> result =
                    graphService.executeMutation(request, accessToken);

            if (result.hasErrors()) {
                throw new GraphException(
                        result.getErrors().stream()
                                .map(Error::getMessage)
                                .reduce((error, error2) -> error.concat("\n").concat(error2))
                                .orElse("Unknown GraphQL Error"),
                        result.getErrors());
            }

            return modelMapper.map(
                    requireNonNull(result.getData()).getUpdateMember(),
                    cricket.merstham.shared.dto.Member.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, AttributeDefinition> getAttributes() {
        var query = new AttributesQuery();
        try {
            Response<AttributesQuery.Data> result = graphService.executeQuery(query);

            return result.getData().getAttributes().stream()
                    .collect(
                            Collectors.toMap(
                                    a -> a.getKey(),
                                    a ->
                                            AttributeDefinition.builder()
                                                    .key(a.getKey())
                                                    .choices(a.getChoices())
                                                    .type(
                                                            AttributeType.valueOf(
                                                                    a.getType().rawValue()))
                                                    .build()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getMemberAttributeString(
            List<MemberAttribute> attributeList, String field, String defaultValue) {
        return attributeList.stream()
                .filter(a -> a.getDefinition().getKey().equals(field))
                .findFirst()
                .map(f -> f.getValue().asText())
                .orElse(defaultValue);
    }
}