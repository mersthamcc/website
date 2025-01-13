package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import cricket.merstham.shared.dto.AttributeDefinition;
import cricket.merstham.shared.dto.Coupon;
import cricket.merstham.shared.dto.Member;
import cricket.merstham.shared.dto.MemberCategory;
import cricket.merstham.shared.dto.MemberSummary;
import cricket.merstham.shared.dto.Order;
import cricket.merstham.shared.dto.RegistrationAction;
import cricket.merstham.shared.dto.UserPaymentMethod;
import cricket.merstham.shared.types.AttributeType;
import cricket.merstham.shared.types.ReportFilter;
import cricket.merstham.website.frontend.exception.GraphException;
import cricket.merstham.website.frontend.model.RegistrationBasket;
import cricket.merstham.website.graph.AddPaymentToOrderMutation;
import cricket.merstham.website.graph.AttributesQuery;
import cricket.merstham.website.graph.CreateMemberSubscriptionMutation;
import cricket.merstham.website.graph.CreateOrderMutation;
import cricket.merstham.website.graph.FilteredMembersQuery;
import cricket.merstham.website.graph.MemberQuery;
import cricket.merstham.website.graph.MembersOwnedByQuery;
import cricket.merstham.website.graph.MembersQuery;
import cricket.merstham.website.graph.MembershipCategoriesQuery;
import cricket.merstham.website.graph.OrderQuery;
import cricket.merstham.website.graph.UpdateMemberMutation;
import cricket.merstham.website.graph.account.AddMemberIdentifierMutation;
import cricket.merstham.website.graph.account.MyMembersQuery;
import cricket.merstham.website.graph.membership.AddPaymentMethodMutation;
import cricket.merstham.website.graph.membership.ConfirmOrderMutation;
import cricket.merstham.website.graph.membership.GetPaymentMethodsQuery;
import cricket.merstham.website.graph.player.DeletePlayCricketLinkMutation;
import cricket.merstham.website.graph.player.PlayCricketLinkMutation;
import cricket.merstham.website.graph.registration.MyCouponsQuery;
import cricket.merstham.website.graph.registration.MyMemberDetailsQuery;
import cricket.merstham.website.graph.type.AttributeInput;
import cricket.merstham.website.graph.type.MemberInput;
import cricket.merstham.website.graph.type.MemberSubscriptionInput;
import cricket.merstham.website.graph.type.PaymentInput;
import cricket.merstham.website.graph.type.StringFilter;
import cricket.merstham.website.graph.type.UserPaymentMethodInput;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cricket.merstham.shared.IdentifierConstants.APPLE_PASS_SERIAL;
import static cricket.merstham.shared.IdentifierConstants.GOOGLE_PASS_SERIAL;
import static cricket.merstham.website.frontend.configuration.CacheConfiguration.MEMBER_SUMMARY_CACHE;
import static cricket.merstham.website.frontend.helpers.AttributeConverter.convert;
import static cricket.merstham.website.frontend.helpers.GraphQLResultHelper.requireGraphData;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

@Service
public class MembershipService {
    private static final Logger LOG = LoggerFactory.getLogger(MembershipService.class);

    private final GraphService graphService;
    private final ModelMapper modelMapper;
    private final int registrationYear;

    @Autowired
    public MembershipService(
            GraphService graphService,
            ModelMapper modelMapper,
            @Value("${registration.current-year}") int registrationYear) {
        this.graphService = graphService;
        this.modelMapper = modelMapper;
        this.registrationYear = registrationYear;
    }

    public Order registerMembersFromBasket(
            RegistrationBasket basket, OAuth2AccessToken accessToken, Locale locale) {
        var createOrder =
                new CreateOrderMutation(
                        basket.getId(),
                        basket.getBasketTotal().doubleValue(),
                        basket.getDiscounts().values().stream()
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                .doubleValue(),
                        Input.fromNullable(
                                basket.getAppliedCoupons().stream().map(Coupon::getCode).toList()));
        Response<CreateOrderMutation.Data> orderResult =
                graphService.executeMutation(createOrder, accessToken);

        var order =
                modelMapper.map(
                        requireGraphData(orderResult, CreateOrderMutation.Data::getCreateOrder),
                        Order.class);

        for (var subscription : basket.getSubscriptions().entrySet()) {
            if (subscription.getValue().getAction() != RegistrationAction.NONE) {
                var memberInput =
                        MemberInput.builder()
                                .memberId(subscription.getValue().getMember().getId())
                                .attributes(
                                        subscription.getValue().getMember().getAttributes().stream()
                                                .map(
                                                        a ->
                                                                AttributeInput.builder()
                                                                        .key(
                                                                                a.getDefinition()
                                                                                        .getKey())
                                                                        .value(a.getValue())
                                                                        .build())
                                                .toList())
                                .subscription(
                                        MemberSubscriptionInput.builder()
                                                .year(registrationYear)
                                                .priceListItemId(
                                                        subscription
                                                                .getValue()
                                                                .getPriceListItem()
                                                                .getId())
                                                .price(
                                                        subscription
                                                                .getValue()
                                                                .getPrice()
                                                                .doubleValue())
                                                .orderId(order.getId())
                                                .addedDate(LocalDate.now())
                                                .build())
                                .build();
                LOG.info(
                        "Processing membership subscription {}, action = {}",
                        subscription.getKey(),
                        subscription.getValue().getAction());
                var createMemberMutation = new CreateMemberSubscriptionMutation(memberInput);
                var member =
                        requireGraphData(
                                graphService.executeMutation(createMemberMutation, accessToken),
                                CreateMemberSubscriptionMutation.Data::getCreateMemberSubscription);

                LOG.info(
                        "Created/updated member {} for subscription {}",
                        member.getId(),
                        subscription.getKey());
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
            String status,
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
                                .status(status)
                                .build());
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
    }

    public List<MemberCategory> getMembershipCategories() {
        var query = new MembershipCategoriesQuery(StringFilter.builder().build());

        Response<MembershipCategoriesQuery.Data> result = graphService.executeQuery(query);
        var categories = result.getData().getMembershipCategories();
        return categories.stream().map(c -> modelMapper.map(c, MemberCategory.class)).toList();
    }

    public MemberCategory getMembershipCategory(String categoryName) {
        var query =
                new MembershipCategoriesQuery(StringFilter.builder().equals(categoryName).build());
        Response<MembershipCategoriesQuery.Data> result = graphService.executeQuery(query);
        return modelMapper.map(
                result.getData().getMembershipCategories().get(0), MemberCategory.class);
    }

    public List<MemberSummary> getAllMembers(OAuth2AccessToken accessToken) {
        var query = new MembersQuery();
        Response<MembersQuery.Data> result = graphService.executeQuery(query, accessToken);
        return result.getData().getMembers().stream()
                .map(m -> modelMapper.map(m, MemberSummary.class))
                .toList();
    }

    public List<MemberSummary> getMembersOwnedBy(
            String ownerSubjectId, OAuth2AccessToken accessToken) {
        var query = MembersOwnedByQuery.builder().owner(ownerSubjectId).build();
        try {
            Response<MembersOwnedByQuery.Data> result =
                    graphService.executeQuery(query, accessToken);
            return result.getData().getMembersOwnedBy().stream()
                    .map(m -> modelMapper.map(m, MemberSummary.class))
                    .toList();
        } catch (Exception e) {
            LOG.atWarn().setCause(e).log("Error getting members owned by {}", ownerSubjectId);
        }
        return List.of();
    }

    @Cacheable(value = MEMBER_SUMMARY_CACHE, key = "#accessToken.tokenValue")
    public List<MemberSummary> getMemberSummary(OAuth2AccessToken accessToken) {
        return getAllMembers(accessToken);
    }

    public List<MemberSummary> getFilteredMemberSummary(
            ReportFilter filter, OAuth2AccessToken accessToken) {
        var query = new FilteredMembersQuery(filter.asText());
        Response<FilteredMembersQuery.Data> result = graphService.executeQuery(query, accessToken);
        return result.getData().getFilteredMembers().stream()
                .map(m -> modelMapper.map(m, MemberSummary.class))
                .toList();
    }

    public Optional<Member> get(int id, OAuth2AccessToken accessToken) {
        var query = new MemberQuery(id);
        Response<MemberQuery.Data> result = graphService.executeQuery(query, accessToken);
        if (isNull(result.getData().getMember())) {
            return Optional.empty();
        }
        return Optional.of(modelMapper.map(result.getData().getMember(), Member.class));
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
                                .toList());
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

        return modelMapper.map(requireNonNull(result.getData()).getUpdateMember(), Member.class);
    }

    public Map<String, AttributeDefinition> getAttributes() {
        var query = new AttributesQuery();
        Response<AttributesQuery.Data> result = graphService.executeQuery(query);

        return result.getData().getAttributes().stream()
                .collect(
                        Collectors.toMap(
                                a -> a.getKey(),
                                a ->
                                        AttributeDefinition.builder()
                                                .key(a.getKey())
                                                .choices(a.getChoices())
                                                .type(AttributeType.valueOf(a.getType().rawValue()))
                                                .build()));
    }

    public Order getOrder(int id) {
        var query = new OrderQuery(id);
        Response<OrderQuery.Data> result = graphService.executeQuery(query);
        return modelMapper.map(result.getData().getOrder(), Order.class);
    }

    public Member linkToPlayCricketPlayer(
            int id, OAuth2AccessToken accessToken, int playCricketId) {
        var request = new PlayCricketLinkMutation(id, playCricketId);
        Response<PlayCricketLinkMutation.Data> result =
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
                requireNonNull(result.getData()).getAssociateMemberToPlayer(), Member.class);
    }

    public Member deletePlayCricketLink(int id, OAuth2AccessToken accessToken) {
        var request = new DeletePlayCricketLinkMutation(id);
        Response<DeletePlayCricketLinkMutation.Data> result =
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
                requireNonNull(result.getData()).getDeleteMemberToPlayerLink(), Member.class);
    }

    public List<MemberSummary> getMyMembers(OAuth2AccessToken accessToken) {
        Response<MyMembersQuery.Data> result =
                graphService.executeQuery(new MyMembersQuery(), accessToken);
        return result.getData().getMyMembers().stream()
                .map(m -> modelMapper.map(m, MemberSummary.class))
                .toList();
    }

    public List<Member> getMyMemberDetails(OAuth2AccessToken accessToken) {
        Response<MyMemberDetailsQuery.Data> result =
                graphService.executeQuery(new MyMemberDetailsQuery(), accessToken);
        return result.getData().getMyMemberDetails().stream()
                .map(m -> modelMapper.map(m, Member.class))
                .toList();
    }

    public void addApplePassSerial(Integer id, String serialNumber, OAuth2AccessToken accessToken) {
        graphService.executeMutation(
                new AddMemberIdentifierMutation(id, APPLE_PASS_SERIAL, serialNumber), accessToken);
    }

    public void addGooglePassSerial(
            Integer id, String serialNumber, OAuth2AccessToken accessToken) {
        graphService.executeMutation(
                new AddMemberIdentifierMutation(id, GOOGLE_PASS_SERIAL, serialNumber), accessToken);
    }

    public List<UserPaymentMethod> getUsersPaymentMethods(
            String owner, OAuth2AccessToken accessToken) throws IOException {
        GetPaymentMethodsQuery query = GetPaymentMethodsQuery.builder().owner(owner).build();
        Response<GetPaymentMethodsQuery.Data> result =
                graphService.executeQuery(query, accessToken);

        return requireGraphData(
                        result,
                        GetPaymentMethodsQuery.Data::getGetPaymentMethods,
                        () -> "Error getting payment methods")
                .stream()
                .map(t -> modelMapper.map(t, UserPaymentMethod.class))
                .toList();
    }

    public UserPaymentMethod createUserPaymentMethod(
            String provider,
            String type,
            String methodId,
            String customerId,
            String status,
            OAuth2AccessToken accessToken)
            throws IOException {
        var input =
                UserPaymentMethodInput.builder()
                        .provider(provider)
                        .type(type)
                        .methodIdentifier(methodId)
                        .customerIdentifier(customerId)
                        .status(status)
                        .createDate(Instant.now())
                        .build();
        var mutation = AddPaymentMethodMutation.builder().paymentMethod(input).build();

        Response<AddPaymentMethodMutation.Data> response =
                graphService.executeMutation(mutation, accessToken);

        return modelMapper.map(
                requireGraphData(
                        response,
                        AddPaymentMethodMutation.Data::getAddPaymentMethod,
                        () -> "Error adding payment method"),
                UserPaymentMethod.class);
    }

    public void confirmOrder(Order order, String paymentType, OAuth2AccessToken accessToken) {
        var mutation =
                ConfirmOrderMutation.builder().id(order.getId()).paymentType(paymentType).build();

        Response<ConfirmOrderMutation.Data> response =
                graphService.executeMutation(mutation, accessToken);

        var result =
                requireGraphData(
                        response,
                        ConfirmOrderMutation.Data::getConfirmOrder,
                        () -> "Error confirming order");
        if (Boolean.FALSE.equals(result.getConfirmed())) {
            LOG.warn("Order {} did not confirm", order.getId());
        }
    }

    public List<Coupon> getAvailableCoupons(OAuth2AccessToken accessToken) {
        var query = MyCouponsQuery.builder().build();
        Response<MyCouponsQuery.Data> response = graphService.executeQuery(query, accessToken);
        return requireGraphData(response, MyCouponsQuery.Data::getMyCoupons).stream()
                .map(c -> modelMapper.map(c, Coupon.class))
                .filter(c -> isNull(c.getRedeemDate()))
                .toList();
    }
}
