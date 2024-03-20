package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Mutation;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import cricket.merstham.shared.dto.AttributeDefinition;
import cricket.merstham.shared.dto.Member;
import cricket.merstham.shared.dto.MemberAttribute;
import cricket.merstham.shared.dto.MemberCategory;
import cricket.merstham.shared.dto.MemberSubscription;
import cricket.merstham.shared.dto.Order;
import cricket.merstham.shared.dto.Price;
import cricket.merstham.shared.dto.PriceListItem;
import cricket.merstham.shared.dto.RegistrationAction;
import cricket.merstham.shared.types.AttributeType;
import cricket.merstham.website.frontend.configuration.ModelMapperConfiguration;
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
import cricket.merstham.website.graph.type.StringFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.verification.VerificationMode;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static cricket.merstham.website.frontend.helpers.GraphServiceMockHelper.mockMutation;
import static cricket.merstham.website.frontend.helpers.GraphServiceMockHelper.mockQuery;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MembershipServiceTest {

    public static final String USER_ID = UUID.randomUUID().toString();
    private static final Lorem LOREM = LoremIpsum.getInstance();
    private static final JsonNodeFactory JSON = JsonNodeFactory.instance;
    private static final Random RANDOM = new SecureRandom();
    private static final VerificationMode ONCE = times(1);

    private static final String GIVEN_NAME = "given-name";
    private static final AttributeDefinition GIVEN_NAME_ATTRIBUTE =
            AttributeDefinition.builder().id(1).key(GIVEN_NAME).type(AttributeType.String).build();
    private static final String FAMILY_NAME = "family-name";
    private static final AttributeDefinition FAMILY_NAME_ATTRIBUTE =
            AttributeDefinition.builder().id(2).key(FAMILY_NAME).type(AttributeType.String).build();
    private static final String EMAIL = "email";
    private static final AttributeDefinition EMAIL_ATTRIBUTE =
            AttributeDefinition.builder().id(3).key(EMAIL).type(AttributeType.String).build();

    private static final List<AttributeDefinition> ATTRIBUTE_DEFINITIONS =
            List.of(GIVEN_NAME_ATTRIBUTE, FAMILY_NAME_ATTRIBUTE, EMAIL_ATTRIBUTE);

    private static final PriceListItem ADULT_PRICE_LIST_ITEM =
            PriceListItem.builder()
                    .currentPrice(BigDecimal.valueOf(120.00))
                    .memberCategory(MemberCategory.builder().id(1).key("adult").build())
                    .id(1)
                    .description("Adult")
                    .includesMatchFees(false)
                    .minAge(18)
                    .price(
                            List.of(
                                    Price.builder()
                                            .price(BigDecimal.valueOf(120.00))
                                            .dateFrom(LocalDate.MIN)
                                            .dateFrom(LocalDate.MAX)
                                            .build()))
                    .build();

    private static final int NUMBER_OF_MEMBERS = 20;
    private final GraphService graphService = mock(GraphService.class);
    private final ModelMapper modelMapper = new ModelMapperConfiguration().modelMapper();
    private final MembershipService service = new MembershipService(graphService, modelMapper);

    @BeforeEach
    void setUp() {}

    @Test
    void shouldCorrectlyRegisterMembersFromBasket() throws IOException {
        var accessToken = createAccessToken();
        var member =
                createMember(
                        LOREM.getFirstName(),
                        LOREM.getLastName(),
                        LOREM.getEmail(),
                        USER_ID,
                        ADULT_PRICE_LIST_ITEM);

        var mutationCaptor = ArgumentCaptor.forClass(Mutation.class);
        var orderId = RANDOM.nextInt(100);
        var subsId = UUID.randomUUID();

        mockMutation(
                graphService,
                mutationCaptor,
                accessToken,
                mutation ->
                        new CreateOrderMutation.Data(
                                new CreateOrderMutation.CreateOrder(
                                        "Order",
                                        orderId,
                                        subsId.toString(),
                                        LocalDate.now(),
                                        USER_ID,
                                        "")),
                mutation ->
                        new CreateMemberMutation.Data(
                                new CreateMemberMutation.CreateMember(
                                        "CreateMember",
                                        1,
                                        Instant.now(),
                                        member.getAttributes().stream()
                                                .map(
                                                        a ->
                                                                new CreateMemberMutation.Attribute(
                                                                        "Attribute",
                                                                        new CreateMemberMutation
                                                                                .Definition(
                                                                                "Definition",
                                                                                a.getDefinition()
                                                                                        .getKey()),
                                                                        a.getValue()))
                                                .toList(),
                                        member.getSubscription().stream()
                                                .map(
                                                        s ->
                                                                new CreateMemberMutation
                                                                        .Subscription(
                                                                        "Subscription",
                                                                        s.getPrice().doubleValue(),
                                                                        new CreateMemberMutation
                                                                                .PriceListItem(
                                                                                "PriceListItem",
                                                                                s.getPriceListItem()
                                                                                        .getId()),
                                                                        s.getYear()))
                                                .toList())));

        var basket =
                new RegistrationBasket(List.of())
                        .setId(subsId.toString())
                        .setSubscriptions(
                                Map.of(
                                        UUID.randomUUID(),
                                        MemberSubscription.builder()
                                                .member(
                                                        Member.builder()
                                                                .attributes(member.getAttributes())
                                                                .type("member")
                                                                .build())
                                                .priceListItem(
                                                        PriceListItem.builder()
                                                                .id(ADULT_PRICE_LIST_ITEM.getId())
                                                                .build())
                                                .action(RegistrationAction.NEW)
                                                .category(
                                                        ADULT_PRICE_LIST_ITEM
                                                                .getMemberCategory()
                                                                .getKey())
                                                .price(ADULT_PRICE_LIST_ITEM.getCurrentPrice())
                                                .build()));

        var result = service.registerMembersFromBasket(basket, accessToken, Locale.getDefault());

        assertThat(result.getId(), equalTo(orderId));
        assertThat(result.getUuid(), equalTo(subsId.toString()));
        assertThat(result.getTotal(), equalTo(basket.getBasketTotal()));

        verify(graphService, ONCE).executeMutation(any(CreateOrderMutation.class), eq(accessToken));
        verify(graphService, ONCE)
                .executeMutation(any(CreateMemberMutation.class), eq(accessToken));

        var orderMutation = (CreateOrderMutation) mutationCaptor.getAllValues().get(0);
        assertThat(orderMutation.variables().uuid(), equalTo(subsId.toString()));

        var memberVariable =
                ((CreateMemberMutation) mutationCaptor.getAllValues().get(1)).variables().member();
        var attributeMap =
                member.getAttributes().stream()
                        .collect(
                                Collectors.toMap(
                                        a -> a.getDefinition().getKey(), a -> a.getValue()));
        var inputAttributeMap =
                memberVariable.attributes().stream()
                        .collect(Collectors.toMap(a -> a.key(), a -> a.value()));
        assertThat(inputAttributeMap, equalTo(attributeMap));

        var subscriptionInput =
                basket.getSubscriptions().values().stream().findFirst().orElseThrow();
        assertThat(
                memberVariable.subscription().price().doubleValue(),
                equalTo(subscriptionInput.getPrice().doubleValue()));
        assertThat(memberVariable.subscription().year(), equalTo(LocalDate.now().getYear()));
        assertThat(memberVariable.subscription().addedDate(), equalTo(LocalDate.now()));
        assertThat(
                memberVariable.subscription().priceListItemId(),
                equalTo(ADULT_PRICE_LIST_ITEM.getId()));
        assertThat(memberVariable.subscription().orderId(), equalTo(orderId));
    }

    @Test
    void shouldCorrectlyCreatePayment() throws IOException {
        var accessToken = createAccessToken();
        var order = Order.builder().id(1).ownerUserId(USER_ID).createDate(LocalDate.now()).build();
        var paymentReference = UUID.randomUUID().toString();
        var mutationCaptor = ArgumentCaptor.forClass(AddPaymentToOrderMutation.class);

        mockMutation(
                graphService,
                mutationCaptor,
                accessToken,
                mutation -> {
                    var payment = mutation.variables().payment();
                    return new AddPaymentToOrderMutation.Data(
                            new AddPaymentToOrderMutation.AddPaymentToOrder(
                                    "AddPaymentToOrder",
                                    1,
                                    payment.type(),
                                    payment.reference(),
                                    payment.date(),
                                    payment.amount().doubleValue(),
                                    payment.processingFees().doubleValue(),
                                    payment.collected(),
                                    payment.reconciled(),
                                    new AddPaymentToOrderMutation.Order(
                                            "Order",
                                            order.getId(),
                                            order.getAccountingId(),
                                            order.getCreateDate(),
                                            order.getUuid())));
                });

        var paymentDate = LocalDateTime.now();
        var result =
                service.createPayment(
                        order,
                        "cash",
                        paymentReference,
                        paymentDate,
                        BigDecimal.TEN,
                        BigDecimal.ONE,
                        true,
                        false,
                        accessToken);

        assertThat(result.getId(), equalTo(order.getId()));
        assertThat(result.getType(), equalTo("cash"));
        assertThat(result.getReference(), equalTo(paymentReference));
        assertThat(result.getDate(), equalTo(paymentDate.toLocalDate()));
        assertThat(result.getAmount(), equalTo(BigDecimal.TEN.doubleValue()));
        assertThat(result.getProcessingFees(), equalTo(BigDecimal.ONE.doubleValue()));
        assertThat(result.getCollected(), equalTo(true));
        assertThat(result.getReconciled(), equalTo(false));
        assertThat(result.getOrder().getId(), equalTo(order.getId()));
        assertThat(result.getOrder().getUuid(), equalTo(order.getUuid()));
        assertThat(result.getOrder().getCreateDate(), equalTo(order.getCreateDate()));
        assertThat(result.getOrder().getAccountingId(), equalTo(order.getAccountingId()));

        var mutation = mutationCaptor.getValue();
        assertThat(mutation.variables().orderId(), equalTo(order.getId()));

        var payment = mutation.variables().payment();
        assertThat(payment.type(), equalTo("cash"));
        assertThat(payment.reference(), equalTo(paymentReference));
        assertThat(payment.date(), equalTo(paymentDate.toLocalDate()));
        assertThat(payment.amount(), equalTo(BigDecimal.TEN.doubleValue()));
        assertThat(payment.processingFees(), equalTo(BigDecimal.ONE.doubleValue()));
        assertThat(payment.collected(), equalTo(true));
        assertThat(payment.reconciled(), equalTo(false));
    }

    @Test
    void shouldCorrectlyGetMembershipCategories() throws IOException {
        var queryCaptor = ArgumentCaptor.forClass(MembershipCategoriesQuery.class);
        mockQuery(
                graphService,
                queryCaptor,
                query ->
                        new MembershipCategoriesQuery.Data(
                                List.of(
                                        new MembershipCategoriesQuery.MembershipCategory(
                                                "MemberCategory",
                                                1,
                                                "adult",
                                                null,
                                                List.of(
                                                        new MembershipCategoriesQuery.PriceListItem(
                                                                "PriceListItem",
                                                                1,
                                                                "Adult Membership",
                                                                18,
                                                                null,
                                                                false,
                                                                Double.valueOf(120.00))),
                                                List.of(
                                                        new MembershipCategoriesQuery.Form(
                                                                "Form",
                                                                10,
                                                                new MembershipCategoriesQuery
                                                                        .Section(
                                                                        "Section",
                                                                        "basics",
                                                                        List.of(
                                                                                new MembershipCategoriesQuery
                                                                                        .Attribute(
                                                                                        "Attribute",
                                                                                        10,
                                                                                        true,
                                                                                        new MembershipCategoriesQuery
                                                                                                .Definition(
                                                                                                "Definition",
                                                                                                "first-name",
                                                                                                cricket
                                                                                                        .merstham
                                                                                                        .website
                                                                                                        .graph
                                                                                                        .type
                                                                                                        .AttributeType
                                                                                                        .STRING,
                                                                                                null)),
                                                                                new MembershipCategoriesQuery
                                                                                        .Attribute(
                                                                                        "Attribute",
                                                                                        NUMBER_OF_MEMBERS,
                                                                                        true,
                                                                                        new MembershipCategoriesQuery
                                                                                                .Definition(
                                                                                                "Definition",
                                                                                                "email",
                                                                                                cricket
                                                                                                        .merstham
                                                                                                        .website
                                                                                                        .graph
                                                                                                        .type
                                                                                                        .AttributeType
                                                                                                        .EMAIL,
                                                                                                null))))))))));
        var result = service.getMembershipCategories();

        assertThat(
                queryCaptor.getValue().variables().key(), equalTo(StringFilter.builder().build()));

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getKey(), equalTo("adult"));
        assertThat(result.get(0).getRegistrationCode(), equalTo(null));
        assertThat(result.get(0).getPriceListItem().size(), equalTo(1));
        assertThat(result.get(0).getForm().size(), equalTo(1));

        var priceListItem = result.get(0).getPriceListItem().get(0);
        assertThat(priceListItem.getDescription(), equalTo("Adult Membership"));
        assertThat(priceListItem.getCurrentPrice(), equalTo(BigDecimal.valueOf(120.0)));
        assertThat(priceListItem.getMinAge(), equalTo(18));
        assertThat(priceListItem.getMaxAge(), equalTo(null));
        assertThat(priceListItem.getIncludesMatchFees(), equalTo(false));

        var form = result.get(0).getForm().get(0);
        assertThat(form.getSortOrder(), equalTo(10));
        assertThat(form.getSection().getKey(), equalTo("basics"));
        assertThat(form.getSection().getAttribute().size(), equalTo(2));

        var attr = form.getSection().getAttribute().get(0);
        assertThat(attr.getSortOrder(), equalTo(10));
        assertThat(attr.getDefinition().getKey(), equalTo("first-name"));
        assertThat(attr.getDefinition().getType(), equalTo(AttributeType.String));
        assertThat(attr.getDefinition().getChoices(), equalTo(null));

        attr = form.getSection().getAttribute().get(1);
        assertThat(attr.getSortOrder(), equalTo(NUMBER_OF_MEMBERS));
        assertThat(attr.getDefinition().getKey(), equalTo("email"));
        assertThat(attr.getDefinition().getType(), equalTo(AttributeType.Email));
        assertThat(attr.getDefinition().getChoices(), equalTo(null));
    }

    @Test
    void shouldCorrectlyGetMembershipCategory() throws IOException {
        var queryCaptor = ArgumentCaptor.forClass(MembershipCategoriesQuery.class);
        mockQuery(
                graphService,
                queryCaptor,
                query ->
                        new MembershipCategoriesQuery.Data(
                                List.of(
                                        new MembershipCategoriesQuery.MembershipCategory(
                                                "MemberCategory",
                                                1,
                                                "adult",
                                                null,
                                                List.of(
                                                        new MembershipCategoriesQuery.PriceListItem(
                                                                "PriceListItem",
                                                                1,
                                                                "Adult Membership",
                                                                18,
                                                                null,
                                                                false,
                                                                Double.valueOf(120.00))),
                                                List.of(
                                                        new MembershipCategoriesQuery.Form(
                                                                "Form",
                                                                10,
                                                                new MembershipCategoriesQuery
                                                                        .Section(
                                                                        "Section",
                                                                        "basics",
                                                                        List.of(
                                                                                new MembershipCategoriesQuery
                                                                                        .Attribute(
                                                                                        "Attribute",
                                                                                        10,
                                                                                        true,
                                                                                        new MembershipCategoriesQuery
                                                                                                .Definition(
                                                                                                "Definition",
                                                                                                "first-name",
                                                                                                cricket
                                                                                                        .merstham
                                                                                                        .website
                                                                                                        .graph
                                                                                                        .type
                                                                                                        .AttributeType
                                                                                                        .STRING,
                                                                                                null)),
                                                                                new MembershipCategoriesQuery
                                                                                        .Attribute(
                                                                                        "Attribute",
                                                                                        NUMBER_OF_MEMBERS,
                                                                                        true,
                                                                                        new MembershipCategoriesQuery
                                                                                                .Definition(
                                                                                                "Definition",
                                                                                                "email",
                                                                                                cricket
                                                                                                        .merstham
                                                                                                        .website
                                                                                                        .graph
                                                                                                        .type
                                                                                                        .AttributeType
                                                                                                        .EMAIL,
                                                                                                null))))))))));
        var result = service.getMembershipCategory("adult");

        assertThat(queryCaptor.getValue().variables().key().equals(), equalTo("adult"));
        assertThat(result.getKey(), equalTo("adult"));
        assertThat(result.getRegistrationCode(), equalTo(null));
        assertThat(result.getPriceListItem().size(), equalTo(1));
        assertThat(result.getForm().size(), equalTo(1));

        var priceListItem = result.getPriceListItem().get(0);
        assertThat(priceListItem.getDescription(), equalTo("Adult Membership"));
        assertThat(priceListItem.getCurrentPrice(), equalTo(BigDecimal.valueOf(120.0)));
        assertThat(priceListItem.getMinAge(), equalTo(18));
        assertThat(priceListItem.getMaxAge(), equalTo(null));
        assertThat(priceListItem.getIncludesMatchFees(), equalTo(false));

        var form = result.getForm().get(0);
        assertThat(form.getSortOrder(), equalTo(10));
        assertThat(form.getSection().getKey(), equalTo("basics"));
        assertThat(form.getSection().getAttribute().size(), equalTo(2));

        var attr = form.getSection().getAttribute().get(0);
        assertThat(attr.getSortOrder(), equalTo(10));
        assertThat(attr.getDefinition().getKey(), equalTo("first-name"));
        assertThat(attr.getDefinition().getType(), equalTo(AttributeType.String));
        assertThat(attr.getDefinition().getChoices(), equalTo(null));

        attr = form.getSection().getAttribute().get(1);
        assertThat(attr.getSortOrder(), equalTo(NUMBER_OF_MEMBERS));
        assertThat(attr.getDefinition().getKey(), equalTo("email"));
        assertThat(attr.getDefinition().getType(), equalTo(AttributeType.Email));
        assertThat(attr.getDefinition().getChoices(), equalTo(null));
    }

    @Test
    void shouldCorrectlyGetAllMembers() throws IOException {
        var accessToken = createAccessToken();
        var queryCaptor = ArgumentCaptor.forClass(MembersQuery.class);

        var members = createAllMembers(NUMBER_OF_MEMBERS);
        mockQuery(graphService, queryCaptor, accessToken, query -> new MembersQuery.Data(members));

        var result = service.getAllMembers(accessToken);

        assertThat(result.size(), equalTo(NUMBER_OF_MEMBERS));
    }

    @Test
    void shouldCorrectlyGetMemberSummary() throws IOException {
        var accessToken = createAccessToken();
        var queryCaptor = ArgumentCaptor.forClass(MembersQuery.class);

        var members = createAllMembers(NUMBER_OF_MEMBERS);

        mockQuery(graphService, queryCaptor, accessToken, query -> new MembersQuery.Data(members));

        var result = service.getMemberSummary(accessToken);

        assertThat(result.size(), equalTo(NUMBER_OF_MEMBERS));
    }

    @ParameterizedTest
    @MethodSource("userIds")
    void shouldCorrectlyGetSpecifiedMember(int memberId) throws IOException {
        var accessToken = createAccessToken();
        var queryCaptor = ArgumentCaptor.forClass(MemberQuery.class);

        var members =
                IntStream.range(1, NUMBER_OF_MEMBERS + 1)
                        .mapToObj(
                                i ->
                                        new MemberQuery.Member(
                                                "Member",
                                                i,
                                                UUID.randomUUID().toString(),
                                                Instant.now(),
                                                List.of(
                                                        new MemberQuery.Attribute(
                                                                "Attribute",
                                                                Instant.now(),
                                                                Instant.now(),
                                                                new MemberQuery.Definition(
                                                                        "AttributeDefinition",
                                                                        "given-name",
                                                                        cricket.merstham.website
                                                                                .graph.type
                                                                                .AttributeType
                                                                                .STRING,
                                                                        null),
                                                                JSON.textNode(
                                                                        LOREM.getFirstName())),
                                                        new MemberQuery.Attribute(
                                                                "Attribute",
                                                                Instant.now(),
                                                                Instant.now(),
                                                                new MemberQuery.Definition(
                                                                        "AttributeDefinition",
                                                                        "family-name",
                                                                        cricket.merstham.website
                                                                                .graph.type
                                                                                .AttributeType
                                                                                .STRING,
                                                                        null),
                                                                JSON.textNode(LOREM.getLastName())),
                                                        new MemberQuery.Attribute(
                                                                "Attribute",
                                                                Instant.now(),
                                                                Instant.now(),
                                                                new MemberQuery.Definition(
                                                                        "AttributeDefinition",
                                                                        "email",
                                                                        cricket.merstham.website
                                                                                .graph.type
                                                                                .AttributeType
                                                                                .EMAIL,
                                                                        null),
                                                                JSON.textNode(LOREM.getEmail()))),
                                                List.of(),
                                                List.of(
                                                        new MemberQuery.Subscription(
                                                                "Subscription",
                                                                LocalDate.now(),
                                                                120.0,
                                                                new MemberQuery.Order(
                                                                        "Order",
                                                                        i,
                                                                        UUID.randomUUID()
                                                                                .toString(),
                                                                        LocalDate.now(),
                                                                        List.of(
                                                                                new MemberQuery
                                                                                        .Payment(
                                                                                        "Payment",
                                                                                        i,
                                                                                        null,
                                                                                        BigDecimal
                                                                                                .TEN
                                                                                                .doubleValue(),
                                                                                        true,
                                                                                        LocalDate
                                                                                                .now(),
                                                                                        BigDecimal
                                                                                                .ONE
                                                                                                .doubleValue(),
                                                                                        false,
                                                                                        UUID.randomUUID()
                                                                                                .toString(),
                                                                                        "card")),
                                                                        UUID.randomUUID()
                                                                                .toString()),
                                                                new MemberQuery.PriceListItem(
                                                                        "Subscription",
                                                                        1,
                                                                        "Adult Membership",
                                                                        false,
                                                                        null,
                                                                        18,
                                                                        new MemberQuery
                                                                                .MemberCategory(
                                                                                "MemberCategory",
                                                                                "adult",
                                                                                List.of())),
                                                                2022))))
                        .toList();

        mockQuery(
                graphService,
                queryCaptor,
                accessToken,
                query ->
                        new MemberQuery.Data(
                                members.stream()
                                        .filter(m -> m.getId() == query.variables().id())
                                        .findFirst()
                                        .orElseThrow()));

        var result = service.get(memberId, accessToken);

        var member = members.get(memberId - 1);
        assertThat(result.get().getId(), equalTo(member.getId()));
        assertThat(result.get().getAttributes().size(), equalTo(member.getAttributes().size()));

        for (int i = 0; i < member.getAttributes().size(); i++) {
            assertThat(
                    result.get().getAttributes().get(i).getValue(),
                    equalTo(member.getAttributes().get(i).getValue()));
            assertThat(
                    result.get().getAttributes().get(i).getCreatedDate(),
                    equalTo(member.getAttributes().get(i).getCreatedDate()));
            assertThat(
                    result.get().getAttributes().get(i).getUpdatedDate(),
                    equalTo(member.getAttributes().get(i).getUpdatedDate()));
            assertThat(
                    result.get().getAttributes().get(i).getDefinition().getKey(),
                    equalTo(member.getAttributes().get(i).getDefinition().getKey()));
            assertThat(
                    result.get().getAttributes().get(i).getDefinition().getType().toString(),
                    equalTo(member.getAttributes().get(i).getDefinition().getType().rawValue()));
            assertThat(
                    result.get().getAttributes().get(i).getDefinition().getChoices(),
                    equalTo(member.getAttributes().get(i).getDefinition().getChoices()));
        }

        assertThat(result.get().getSubscription().size(), equalTo(member.getSubscription().size()));
        for (int i = 0; i < member.getSubscription().size(); i++) {
            assertThat(
                    result.get().getSubscription().get(i).getYear(),
                    equalTo(member.getSubscription().get(i).getYear()));
            assertThat(
                    result.get().getSubscription().get(i).getAddedDate(),
                    equalTo(member.getSubscription().get(i).getAddedDate()));
            assertThat(
                    result.get().getSubscription().get(i).getPrice().doubleValue(),
                    equalTo(member.getSubscription().get(i).getPrice()));
            assertThat(
                    result.get().getSubscription().get(i).getPriceListItem().getDescription(),
                    equalTo(member.getSubscription().get(i).getPriceListItem().getDescription()));
            assertThat(
                    result.get()
                            .getSubscription()
                            .get(i)
                            .getPriceListItem()
                            .getMemberCategory()
                            .getKey(),
                    equalTo(
                            member.getSubscription()
                                    .get(i)
                                    .getPriceListItem()
                                    .getMemberCategory()
                                    .getKey()));
        }
        assertThat(result.get().getOwnerUserId(), equalTo(member.getOwnerUserId()));
        assertThat(result.get().getRegistrationDate(), equalTo(member.getRegistrationDate()));
    }

    @Test
    void shouldReturnEmptyIfMemberDoesNotExist() throws IOException {
        var accessToken = createAccessToken();
        var queryCaptor = ArgumentCaptor.forClass(MemberQuery.class);

        mockQuery(graphService, queryCaptor, accessToken, query -> new MemberQuery.Data(null));

        var result = service.get(10, accessToken);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCorrectlyUpdateMember() throws IOException {
        var accessToken = createAccessToken();
        var mutationCaptor = ArgumentCaptor.forClass(UpdateMemberMutation.class);
        var queryCaptor = ArgumentCaptor.forClass(AttributesQuery.class);
        var existingData =
                List.of(
                        MemberAttribute.builder()
                                .memberId(10l)
                                .definition(GIVEN_NAME_ATTRIBUTE)
                                .value(JSON.textNode(LOREM.getFirstName()))
                                .createdDate(Instant.now())
                                .updatedDate(Instant.now())
                                .build(),
                        MemberAttribute.builder()
                                .memberId(10l)
                                .definition(FAMILY_NAME_ATTRIBUTE)
                                .value(JSON.textNode(LOREM.getLastName()))
                                .createdDate(Instant.now())
                                .updatedDate(Instant.now())
                                .build(),
                        MemberAttribute.builder()
                                .memberId(10l)
                                .definition(EMAIL_ATTRIBUTE)
                                .value(JSON.textNode(LOREM.getEmail()))
                                .createdDate(Instant.now())
                                .updatedDate(Instant.now())
                                .build());

        var data = new LinkedMultiValueMap<String, Object>();
        data.put(EMAIL, List.of(LOREM.getEmail()));
        data.put(GIVEN_NAME, List.of(LOREM.getFirstName()));

        mockQuery(
                graphService,
                queryCaptor,
                invocation ->
                        new AttributesQuery.Data(
                                ATTRIBUTE_DEFINITIONS.stream()
                                        .map(
                                                a ->
                                                        new AttributesQuery.Attribute(
                                                                "Attribute",
                                                                a.getKey(),
                                                                cricket.merstham.website.graph.type
                                                                        .AttributeType.valueOf(
                                                                        a.getType()
                                                                                .toString()
                                                                                .toUpperCase()),
                                                                a.getChoices()))
                                        .toList()));
        mockMutation(
                graphService,
                mutationCaptor,
                accessToken,
                invocation -> {
                    var mapped =
                            invocation.variables().data().stream()
                                    .collect(
                                            Collectors.toMap(
                                                    AttributeInput::key, AttributeInput::value));
                    var mergedData =
                            existingData.stream()
                                    .map(
                                            a ->
                                                    new UpdateMemberMutation.Attribute(
                                                            "Attribute",
                                                            a.getCreatedDate(),
                                                            a.getUpdatedDate(),
                                                            new UpdateMemberMutation.Definition(
                                                                    "AttributeDefinition",
                                                                    a.getDefinition().getKey(),
                                                                    cricket.merstham.website.graph
                                                                            .type.AttributeType
                                                                            .valueOf(
                                                                                    a.getDefinition()
                                                                                            .getType()
                                                                                            .toString()
                                                                                            .toUpperCase()),
                                                                    a.getDefinition().getChoices()),
                                                            mapped.containsKey(
                                                                            a.getDefinition()
                                                                                    .getKey())
                                                                    ? mapped.get(
                                                                            a.getDefinition()
                                                                                    .getKey())
                                                                    : a.getValue()))
                                    .toList();
                    return new UpdateMemberMutation.Data(
                            new UpdateMemberMutation.UpdateMember(
                                    "Member",
                                    invocation.variables().id(),
                                    UUID.randomUUID().toString(),
                                    Instant.now(),
                                    mergedData,
                                    List.of()));
                });

        var result = service.update(10, accessToken, Locale.UK, data);

        assertThat(result.getId(), equalTo(10));
        assertThat(result.getAttributes().size(), equalTo(existingData.size()));

        assertThat(
                result.getAttributes().get(0).getDefinition().getKey(),
                equalTo(existingData.get(0).getDefinition().getKey()));
        assertThat(
                result.getAttributes().get(0).getDefinition().getType(),
                equalTo(existingData.get(0).getDefinition().getType()));
        assertThat(
                result.getAttributes().get(0).getValue().asText(),
                equalTo(data.get(GIVEN_NAME).get(0)));

        assertThat(
                result.getAttributes().get(1).getDefinition().getKey(),
                equalTo(existingData.get(1).getDefinition().getKey()));
        assertThat(
                result.getAttributes().get(1).getDefinition().getType(),
                equalTo(existingData.get(1).getDefinition().getType()));
        assertThat(
                result.getAttributes().get(1).getValue(), equalTo(existingData.get(1).getValue()));

        assertThat(
                result.getAttributes().get(2).getDefinition().getKey(),
                equalTo(existingData.get(2).getDefinition().getKey()));
        assertThat(
                result.getAttributes().get(2).getDefinition().getType(),
                equalTo(existingData.get(2).getDefinition().getType()));
        assertThat(
                result.getAttributes().get(2).getValue().asText(), equalTo(data.get(EMAIL).get(0)));
    }

    @Test
    void shouldCorrectlyGetAttributes() throws IOException {
        var queryCaptor = ArgumentCaptor.forClass(AttributesQuery.class);

        mockQuery(
                graphService,
                queryCaptor,
                invocation ->
                        new AttributesQuery.Data(
                                ATTRIBUTE_DEFINITIONS.stream()
                                        .map(
                                                a ->
                                                        new AttributesQuery.Attribute(
                                                                "Attribute",
                                                                a.getKey(),
                                                                cricket.merstham.website.graph.type
                                                                        .AttributeType.valueOf(
                                                                        a.getType()
                                                                                .toString()
                                                                                .toUpperCase()),
                                                                a.getChoices()))
                                        .toList()));

        var result = service.getAttributes();

        assertThat(result.size(), equalTo(ATTRIBUTE_DEFINITIONS.size()));

        result.entrySet()
                .forEach(
                        a -> {
                            assertThat(
                                    a.getValue().getKey(),
                                    equalTo(
                                            ATTRIBUTE_DEFINITIONS.stream()
                                                    .filter(i -> i.getKey().equals(a.getKey()))
                                                    .findFirst()
                                                    .orElseThrow()
                                                    .getKey()));
                            assertThat(
                                    a.getValue().getChoices(),
                                    equalTo(
                                            ATTRIBUTE_DEFINITIONS.stream()
                                                    .filter(i -> i.getKey().equals(a.getKey()))
                                                    .findFirst()
                                                    .orElseThrow()
                                                    .getChoices()));
                            assertThat(
                                    a.getValue().getType(),
                                    equalTo(
                                            ATTRIBUTE_DEFINITIONS.stream()
                                                    .filter(i -> i.getKey().equals(a.getKey()))
                                                    .findFirst()
                                                    .orElseThrow()
                                                    .getType()));
                        });
    }

    public static Stream<Arguments> userIds() {
        return IntStream.range(1, NUMBER_OF_MEMBERS + 1).mapToObj(Arguments::of);
    }

    private Member createMember(
            String firstName,
            String lastName,
            String email,
            String userId,
            PriceListItem priceListItem) {
        var id = RANDOM.nextInt(100);
        var now = Instant.now();
        return Member.builder()
                .id(id)
                .attributes(
                        List.of(
                                MemberAttribute.builder()
                                        .memberId((long) id)
                                        .createdDate(now)
                                        .updatedDate(now)
                                        .definition(GIVEN_NAME_ATTRIBUTE)
                                        .value(JSON.textNode(firstName))
                                        .build(),
                                MemberAttribute.builder()
                                        .memberId((long) id)
                                        .createdDate(now)
                                        .updatedDate(now)
                                        .definition(FAMILY_NAME_ATTRIBUTE)
                                        .value(JSON.textNode(lastName))
                                        .build(),
                                MemberAttribute.builder()
                                        .memberId((long) id)
                                        .createdDate(now)
                                        .updatedDate(now)
                                        .definition(EMAIL_ATTRIBUTE)
                                        .value(JSON.textNode(email))
                                        .build()))
                .registrationDate(now)
                .ownerUserId(userId)
                .subscription(
                        List.of(
                                MemberSubscription.builder()
                                        .addedDate(LocalDate.ofInstant(now, ZoneId.systemDefault()))
                                        .year(
                                                LocalDate.ofInstant(now, ZoneId.systemDefault())
                                                        .getYear())
                                        .priceListItem(priceListItem)
                                        .price(priceListItem.getCurrentPrice())
                                        .build()))
                .type("member")
                .build();
    }

    private List<MembersQuery.Member> createAllMembers(int numberOfMembers) {
        return IntStream.range(1, numberOfMembers + 1)
                .mapToObj(
                        i ->
                                new MembersQuery.Member(
                                        "MemberSummary",
                                        i,
                                        LOREM.getLastName(),
                                        LOREM.getFirstName(),
                                        Instant.now().minus(30, ChronoUnit.DAYS),
                                        LocalDate.now().minus(20, ChronoUnit.YEARS),
                                        "Adult",
                                        "MALE",
                                        LocalDate.now().getYear(),
                                        LocalDate.now().minus(3, ChronoUnit.MONTHS),
                                        120.0,
                                        "Subscription",
                                        120.0,
                                        "cash",
                                        "Adult Membership",
                                        List.of()))
                .sorted(Comparator.comparing(MembersQuery.Member::getId))
                .toList();
    }

    private OAuth2AccessToken createAccessToken() {
        var accessToken = mock(OAuth2AccessToken.class);
        return accessToken;
    }
}
