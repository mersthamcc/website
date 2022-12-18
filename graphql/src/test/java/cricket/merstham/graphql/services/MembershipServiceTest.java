package cricket.merstham.graphql.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import cricket.merstham.graphql.configuration.ModelMapperConfiguration;
import cricket.merstham.graphql.entity.AttributeDefinitionEntity;
import cricket.merstham.graphql.entity.MemberAttributeEntity;
import cricket.merstham.graphql.entity.MemberAttributeEntityId;
import cricket.merstham.graphql.entity.MemberCategoryEntity;
import cricket.merstham.graphql.entity.MemberEntity;
import cricket.merstham.graphql.entity.MemberSubscriptionEntity;
import cricket.merstham.graphql.entity.MemberSubscriptionEntityId;
import cricket.merstham.graphql.entity.OrderEntity;
import cricket.merstham.graphql.entity.PaymentEntity;
import cricket.merstham.graphql.entity.PricelistEntity;
import cricket.merstham.graphql.entity.PricelistEntityId;
import cricket.merstham.graphql.entity.PricelistItemEntity;
import cricket.merstham.graphql.inputs.MemberInput;
import cricket.merstham.graphql.inputs.MemberSubscriptionInput;
import cricket.merstham.graphql.inputs.PaymentInput;
import cricket.merstham.graphql.inputs.filters.StringFilter;
import cricket.merstham.graphql.inputs.where.MemberCategoryWhereInput;
import cricket.merstham.graphql.repository.AttributeDefinitionEntityRepository;
import cricket.merstham.graphql.repository.MemberCategoryEntityRepository;
import cricket.merstham.graphql.repository.MemberEntityRepository;
import cricket.merstham.graphql.repository.OrderEntityRepository;
import cricket.merstham.graphql.repository.PaymentEntityRepository;
import cricket.merstham.graphql.repository.PriceListItemEntityRepository;
import cricket.merstham.shared.dto.Member;
import cricket.merstham.shared.dto.Order;
import cricket.merstham.shared.types.AttributeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MembershipServiceTest {

    private static final Lorem LOREM = LoremIpsum.getInstance();
    private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

    private static final List<AttributeDefinitionEntity> ATTRIBUTES =
            List.of(
                    AttributeDefinitionEntity.builder()
                            .id(1)
                            .key("firstname")
                            .type(AttributeType.String)
                            .build(),
                    AttributeDefinitionEntity.builder()
                            .id(2)
                            .key("lastname")
                            .type(AttributeType.String)
                            .build(),
                    AttributeDefinitionEntity.builder()
                            .id(3)
                            .key("email")
                            .type(AttributeType.Email)
                            .build(),
                    AttributeDefinitionEntity.builder()
                            .id(4)
                            .key("date")
                            .type(AttributeType.Date)
                            .build(),
                    AttributeDefinitionEntity.builder()
                            .id(5)
                            .key("number")
                            .type(AttributeType.Number)
                            .build(),
                    AttributeDefinitionEntity.builder()
                            .id(6)
                            .key("boolean")
                            .type(AttributeType.Boolean)
                            .build(),
                    AttributeDefinitionEntity.builder()
                            .id(7)
                            .key("list")
                            .type(AttributeType.List)
                            .choices(
                                    JSON.arrayNode()
                                            .add("List Option 1")
                                            .add("List Option 2")
                                            .add("List Option 3"))
                            .build(),
                    AttributeDefinitionEntity.builder()
                            .id(8)
                            .key("choice")
                            .type(AttributeType.Option)
                            .choices(
                                    JSON.arrayNode()
                                            .add("Choice 1")
                                            .add("Choice 2")
                                            .add("Choice 3")
                                            .add("Choice 4"))
                            .build(),
                    AttributeDefinitionEntity.builder()
                            .id(9)
                            .key("time")
                            .type(AttributeType.Time)
                            .build(),
                    AttributeDefinitionEntity.builder()
                            .id(10)
                            .key("timestamp")
                            .type(AttributeType.Timestamp)
                            .build());

    private static final List<PricelistItemEntity> PRICELIST_ITEM_ENTITIES =
            List.of(
                    PricelistItemEntity.builder()
                            .description("Adult")
                            .id(1)
                            .minAge(18)
                            .priceList(
                                    List.of(
                                            PricelistEntity.builder()
                                                    .price(BigDecimal.valueOf(100))
                                                    .primaryKey(
                                                            PricelistEntityId.builder()
                                                                    .dateFrom(LocalDate.MIN)
                                                                    .dateTo(LocalDate.MAX)
                                                                    .build())
                                                    .build()))
                            .build(),
                    PricelistItemEntity.builder()
                            .description("Junior")
                            .id(2)
                            .minAge(6)
                            .maxAge(17)
                            .priceList(
                                    List.of(
                                            PricelistEntity.builder()
                                                    .price(BigDecimal.valueOf(80))
                                                    .primaryKey(
                                                            PricelistEntityId.builder()
                                                                    .dateFrom(LocalDate.MIN)
                                                                    .dateTo(LocalDate.MAX)
                                                                    .build())
                                                    .build()))
                            .build());

    private static final List<MemberEntity> MEMBERS =
            List.of(
                    MemberEntity.builder()
                            .id(0)
                            .ownerUserId(UUID.randomUUID().toString())
                            .registrationDate(Instant.now())
                            .attributes(
                                    List.of(
                                            MemberAttributeEntity.builder()
                                                    .createdDate(Instant.now())
                                                    .primaryKey(
                                                            MemberAttributeEntityId.builder()
                                                                    .definition(ATTRIBUTES.get(0))
                                                                    .build())
                                                    .value(JSON.textNode(LOREM.getFirstName()))
                                                    .build(),
                                            MemberAttributeEntity.builder()
                                                    .createdDate(Instant.now())
                                                    .primaryKey(
                                                            MemberAttributeEntityId.builder()
                                                                    .definition(ATTRIBUTES.get(1))
                                                                    .build())
                                                    .value(JSON.textNode(LOREM.getLastName()))
                                                    .build(),
                                            MemberAttributeEntity.builder()
                                                    .createdDate(Instant.now())
                                                    .primaryKey(
                                                            MemberAttributeEntityId.builder()
                                                                    .definition(ATTRIBUTES.get(2))
                                                                    .build())
                                                    .value(JSON.textNode(LOREM.getEmail()))
                                                    .build()))
                            .subscription(
                                    List.of(
                                            MemberSubscriptionEntity.builder()
                                                    .addedDate(LocalDate.now())
                                                    .price(
                                                            PRICELIST_ITEM_ENTITIES
                                                                    .get(0)
                                                                    .getCurrentPrice())
                                                    .primaryKey(
                                                            MemberSubscriptionEntityId.builder()
                                                                    .year(LocalDate.now().getYear())
                                                                    .build())
                                                    .pricelistItem(PRICELIST_ITEM_ENTITIES.get(0))
                                                    .build()))
                            .type("member")
                            .build(),
                    MemberEntity.builder()
                            .id(1)
                            .ownerUserId(UUID.randomUUID().toString())
                            .registrationDate(Instant.now())
                            .attributes(
                                    List.of(
                                            MemberAttributeEntity.builder()
                                                    .createdDate(Instant.now())
                                                    .primaryKey(
                                                            MemberAttributeEntityId.builder()
                                                                    .definition(ATTRIBUTES.get(0))
                                                                    .build())
                                                    .value(JSON.textNode(LOREM.getFirstName()))
                                                    .build(),
                                            MemberAttributeEntity.builder()
                                                    .createdDate(Instant.now())
                                                    .primaryKey(
                                                            MemberAttributeEntityId.builder()
                                                                    .definition(ATTRIBUTES.get(1))
                                                                    .build())
                                                    .value(JSON.textNode(LOREM.getLastName()))
                                                    .build(),
                                            MemberAttributeEntity.builder()
                                                    .createdDate(Instant.now())
                                                    .primaryKey(
                                                            MemberAttributeEntityId.builder()
                                                                    .definition(ATTRIBUTES.get(2))
                                                                    .build())
                                                    .value(JSON.textNode(LOREM.getEmail()))
                                                    .build()))
                            .subscription(
                                    List.of(
                                            MemberSubscriptionEntity.builder()
                                                    .addedDate(LocalDate.now())
                                                    .price(
                                                            PRICELIST_ITEM_ENTITIES
                                                                    .get(1)
                                                                    .getCurrentPrice())
                                                    .primaryKey(
                                                            MemberSubscriptionEntityId.builder()
                                                                    .year(LocalDate.now().getYear())
                                                                    .build())
                                                    .pricelistItem(PRICELIST_ITEM_ENTITIES.get(1))
                                                    .build()))
                            .type("member")
                            .build());
    private static final List<MemberCategoryEntity> MEMBER_CATEGORIES =
            List.of(
                    MemberCategoryEntity.builder()
                            .key("adult")
                            .pricelistItem(List.of(PRICELIST_ITEM_ENTITIES.get(0)))
                            .form(List.of())
                            .build(),
                    MemberCategoryEntity.builder()
                            .key("junior")
                            .pricelistItem(List.of(PRICELIST_ITEM_ENTITIES.get(1)))
                            .form(List.of())
                            .build());

    private static final List<OrderEntity> ORDERS =
            List.of(
                    OrderEntity.builder()
                            .id(0)
                            .uuid(UUID.randomUUID().toString())
                            .accountingId(UUID.randomUUID().toString())
                            .createDate(LocalDate.now())
                            .memberSubscription(MEMBERS.get(0).getSubscription())
                            .ownerUserId(UUID.randomUUID().toString())
                            .payment(
                                    new ArrayList<>(
                                            List.of(
                                                    PaymentEntity.builder()
                                                            .id(0)
                                                            .collected(true)
                                                            .reference(UUID.randomUUID().toString())
                                                            .amount(
                                                                    MEMBERS.get(0)
                                                                            .getSubscription()
                                                                            .get(0)
                                                                            .getPrice())
                                                            .processingFees(
                                                                    MEMBERS.get(0)
                                                                            .getSubscription()
                                                                            .get(0)
                                                                            .getPrice()
                                                                            .multiply(
                                                                                    BigDecimal
                                                                                            .valueOf(
                                                                                                    0.2)))
                                                            .accountingId(
                                                                    UUID.randomUUID().toString())
                                                            .feesAccountingId(
                                                                    UUID.randomUUID().toString())
                                                            .reconciled(true)
                                                            .type("cash")
                                                            .build())))
                            .build(),
                    OrderEntity.builder()
                            .id(1)
                            .uuid(UUID.randomUUID().toString())
                            .accountingId(UUID.randomUUID().toString())
                            .createDate(LocalDate.now())
                            .memberSubscription(MEMBERS.get(1).getSubscription())
                            .ownerUserId(UUID.randomUUID().toString())
                            .payment(
                                    new ArrayList<>(
                                            List.of(
                                                    PaymentEntity.builder()
                                                            .id(1)
                                                            .collected(true)
                                                            .reference(UUID.randomUUID().toString())
                                                            .amount(
                                                                    MEMBERS.get(1)
                                                                            .getSubscription()
                                                                            .get(0)
                                                                            .getPrice())
                                                            .processingFees(
                                                                    MEMBERS.get(1)
                                                                            .getSubscription()
                                                                            .get(0)
                                                                            .getPrice()
                                                                            .multiply(
                                                                                    BigDecimal
                                                                                            .valueOf(
                                                                                                    0.2)))
                                                            .accountingId(
                                                                    UUID.randomUUID().toString())
                                                            .feesAccountingId(
                                                                    UUID.randomUUID().toString())
                                                            .reconciled(false)
                                                            .type("card")
                                                            .build())))
                            .build());
    private final AttributeDefinitionEntityRepository attributeRepository =
            mock(AttributeDefinitionEntityRepository.class);
    private final MemberEntityRepository memberRepository = mock(MemberEntityRepository.class);
    private final MemberCategoryEntityRepository memberCategoryEntityRepository =
            mock(MemberCategoryEntityRepository.class);
    private final OrderEntityRepository orderEntityRepository = mock(OrderEntityRepository.class);
    private final PaymentEntityRepository paymentEntityRepository =
            mock(PaymentEntityRepository.class);
    private final PriceListItemEntityRepository priceListItemEntityRepository =
            mock(PriceListItemEntityRepository.class);

    private final MembershipService service =
            new MembershipService(
                    attributeRepository,
                    memberRepository,
                    memberCategoryEntityRepository,
                    orderEntityRepository,
                    paymentEntityRepository,
                    priceListItemEntityRepository,
                    new ModelMapperConfiguration().modelMapper());

    @BeforeEach
    void setup() {
        when(attributeRepository.findAll()).thenReturn(ATTRIBUTES);
        when(memberRepository.findAll()).thenReturn(MEMBERS);
        when(priceListItemEntityRepository.findAll()).thenReturn(PRICELIST_ITEM_ENTITIES);
        when(memberCategoryEntityRepository.findAll()).thenReturn(MEMBER_CATEGORIES);
        when(memberRepository.findById(anyInt()))
                .then(
                        invocation ->
                                MEMBERS.stream()
                                        .filter(
                                                m ->
                                                        Objects.equals(
                                                                m.getId(),
                                                                invocation.getArgument(0)))
                                        .findFirst());
        var year = LocalDate.now().getYear();
        when(orderEntityRepository.findByCreateDateBetween(
                        any(LocalDate.class), any(LocalDate.class)))
                .then(
                        invocation ->
                                ORDERS.stream()
                                        .filter(
                                                o ->
                                                        !(o.getCreateDate()
                                                                        .isBefore(
                                                                                invocation
                                                                                        .getArgument(
                                                                                                0))
                                                                || o.getCreateDate()
                                                                        .isAfter(
                                                                                invocation
                                                                                        .getArgument(
                                                                                                1))))
                                        .collect(Collectors.toList()));
        when(orderEntityRepository.findByOwnerUserIdAllIgnoreCaseOrderByCreateDateAsc(any()))
                .then(
                        invocation ->
                                ORDERS.stream()
                                        .filter(
                                                o ->
                                                        Objects.equals(
                                                                o.getOwnerUserId(),
                                                                invocation.getArgument(0)))
                                        .collect(Collectors.toList()));
        when(orderEntityRepository.findById(anyInt()))
                .then(
                        invocation ->
                                ORDERS.stream()
                                        .filter(
                                                o ->
                                                        Objects.equals(
                                                                o.getId(),
                                                                invocation.getArgument(0)))
                                        .findFirst());

        when(priceListItemEntityRepository.findById(PRICELIST_ITEM_ENTITIES.get(0).getId()))
                .thenReturn(Optional.of(PRICELIST_ITEM_ENTITIES.get(0)));
        when(memberRepository.saveAndFlush(any(MemberEntity.class))).then(returnsFirstArg());
    }

    @Test
    void shouldReturnCorrectListOfAttributes() {
        var result = service.getAttributes();

        assertThat(result.size(), equalTo(ATTRIBUTES.size()));

        for (int i = 0; i < ATTRIBUTES.size(); i++) {
            assertThat(result.get(i).getId(), equalTo(ATTRIBUTES.get(i).getId()));
            assertThat(result.get(i).getKey(), equalTo(ATTRIBUTES.get(i).getKey()));
            assertThat(result.get(i).getType(), equalTo(ATTRIBUTES.get(i).getType()));
            assertThat(
                    result.get(i).getChoices(),
                    equalTo(choiceList(ATTRIBUTES.get(i).getChoices())));
        }
    }

    @Test
    void shouldReturnAllMembersWhenGetMembersCalled() {
        var result = service.getMembers();

        assertThat(result.size(), equalTo(MEMBERS.size()));

        for (int i = 0; i < MEMBERS.size(); i++) {
            assertMemberMatchesEntity(result.get(i), MEMBERS.get(i));
        }
    }

    @Test
    void shouldReturnAllCategoriesWhenGetCategoriesCalledWithNull() {
        var result = service.getCategories(null);

        assertThat(result.size(), equalTo(MEMBER_CATEGORIES.size()));

        for (int i = 0; i < result.size(); i++) {
            assertThat(result.get(i).getKey(), equalTo(MEMBER_CATEGORIES.get(i).getKey()));
            assertThat(
                    result.get(i).getRegistrationCode(),
                    equalTo(MEMBER_CATEGORIES.get(i).getRegistrationCode()));
            assertThat(result.get(i).getId(), equalTo(MEMBER_CATEGORIES.get(i).getId()));
        }
    }

    @Test
    void shouldReturnFilteredCategoriesWhenGetCategoriesCalledWithWhereClause() {
        var where = mock(MemberCategoryWhereInput.class);
        var keyFilter = mock(StringFilter.class);
        when(keyFilter.matches("adult")).thenReturn(true);
        when(where.getKey()).thenReturn(keyFilter);
        when(where.matches(any())).thenCallRealMethod();

        var result = service.getCategories(where);

        assertThat(result.size(), equalTo(1));

        assertThat(result.get(0).getKey(), equalTo(MEMBER_CATEGORIES.get(0).getKey()));
        assertThat(
                result.get(0).getRegistrationCode(),
                equalTo(MEMBER_CATEGORIES.get(0).getRegistrationCode()));
        assertThat(result.get(0).getId(), equalTo(MEMBER_CATEGORIES.get(0).getId()));
    }

    @Test
    void shouldReturnCorrectMemberAndAllMappedAttribute() {
        final int ID = 1;
        var result = service.getMember(ID);

        assertMemberMatchesEntity(result, MEMBERS.get(ID));
    }

    @Test
    void shouldReturnListOfThisYearsOrders() {
        var result = service.getOrders(LocalDate.now().getYear());

        assertThat(result.size(), equalTo(ORDERS.size()));

        for (int i = 0; i < ORDERS.size(); i++) {
            assertThatOrderMatchesEntity(result.get(i), ORDERS.get(i));
        }
    }

    @Test
    void shouldReturnListOfOrdersBelongingToSpecifiedUser() {
        var userId = ORDERS.get(0).getOwnerUserId();
        var principal = mock(JwtAuthenticationToken.class);
        when(principal.getName()).thenReturn(userId);
        var result = service.getMyOrders(principal);

        assertThat(result.size(), equalTo(1));

        assertThatOrderMatchesEntity(result.get(0), ORDERS.get(0));
    }

    @Test
    void shouldReturnNewlyCreatedMember() {
        var principal = mock(JwtAuthenticationToken.class);
        when(principal.getName()).thenReturn(UUID.randomUUID().toString());

        var input =
                MemberInput.builder()
                        .attributes(List.of())
                        .registrationDate(Instant.now())
                        .subscription(
                                MemberSubscriptionInput.builder()
                                        .orderId(ORDERS.get(0).getId())
                                        .priceListItemId(PRICELIST_ITEM_ENTITIES.get(0).getId())
                                        .addedDate(LocalDate.now())
                                        .price(PRICELIST_ITEM_ENTITIES.get(0).getCurrentPrice())
                                        .year(LocalDate.now().getYear())
                                        .build())
                        .build();

        var result = service.createMember(input, principal);
    }

    @Test
    void shouldReturnNewlyCreatedOrder() {
        var orderUuid = UUID.randomUUID().toString();
        var userId = UUID.randomUUID().toString();
        var principal = mock(JwtAuthenticationToken.class);
        when(principal.getName()).thenReturn(userId);

        when(orderEntityRepository.saveAndFlush(any(OrderEntity.class)))
                .then(
                        invocation ->
                                OrderEntity.builder()
                                        .id(ORDERS.size())
                                        .uuid(((OrderEntity) invocation.getArgument(0)).getUuid())
                                        .createDate(
                                                ((OrderEntity) invocation.getArgument(0))
                                                        .getCreateDate())
                                        .ownerUserId(
                                                ((OrderEntity) invocation.getArgument(0))
                                                        .getOwnerUserId())
                                        .build());

        var result = service.createOrder(orderUuid, principal);

        assertThat(result.getId(), equalTo(ORDERS.size()));
        assertThat(result.getUuid(), equalTo(orderUuid));
        assertThat(result.getCreateDate(), equalTo(LocalDate.now()));
        assertThat(result.getOwnerUserId(), equalTo(userId));
    }

    @Test
    void shouldReturnNewlyCreatedPayment() {
        var userId = UUID.randomUUID().toString();
        var principal = mock(JwtAuthenticationToken.class);
        when(principal.getName()).thenReturn(userId);

        var order = ORDERS.get(0);

        var input =
                PaymentInput.builder()
                        .amount(order.getMemberSubscription().get(0).getPrice())
                        .date(order.getCreateDate())
                        .reference(UUID.randomUUID().toString())
                        .type("cash")
                        .reconciled(false)
                        .collected(true)
                        .processingFees(BigDecimal.ONE)
                        .build();
        when(paymentEntityRepository.saveAndFlush(any(PaymentEntity.class)))
                .then(
                        invocation -> {
                            var entity = (PaymentEntity) invocation.getArgument(0);
                            var payment =
                                    PaymentEntity.builder()
                                            .id(10)
                                            .amount(entity.getAmount())
                                            .date(entity.getDate())
                                            .reference(entity.getReference())
                                            .type(entity.getType())
                                            .reconciled(entity.getReconciled())
                                            .collected(entity.getCollected())
                                            .processingFees(entity.getProcessingFees())
                                            .order(order)
                                            .build();
                            order.getPayment().add(payment);
                            return payment;
                        });
        var result = service.addPaymentToOrder(1, input, principal);

        assertThat(result.getId(), equalTo(10));
        assertThat(result.getDate(), equalTo(input.getDate()));
        assertThat(result.getType(), equalTo(input.getType()));
        assertThat(result.getReference(), equalTo(input.getReference()));
        assertThat(result.getAmount(), equalTo(input.getAmount()));
        assertThat(result.getProcessingFees(), equalTo(input.getProcessingFees()));
        assertThat(result.getReconciled(), equalTo(input.isReconciled()));
        assertThat(result.getCollected(), equalTo(input.isCollected()));

        assertThatOrderMatchesEntity(result.getOrder(), order);
        assertThat(result.getOrder().getPayment().size(), equalTo(2));
    }

    private List<String> choiceList(JsonNode choices) {
        List<String> list = new ArrayList<>();
        if (isNull(choices)) return null;
        if (choices.isArray()) {
            for (Iterator<JsonNode> it = choices.elements(); it.hasNext(); ) {
                JsonNode node = it.next();

                list.add(node.asText());
            }
        }
        return list;
    }

    private void assertMemberMatchesEntity(Member member, MemberEntity memberEntity) {
        assertThat(member.getId(), equalTo(memberEntity.getId()));
        assertThat(member.getType(), equalTo(memberEntity.getType()));
        assertThat(member.getOwnerUserId(), equalTo(memberEntity.getOwnerUserId()));
        var attributes = member.getAttributes();
        for (int j = 0; j < attributes.size(); j++) {
            assertThat(
                    attributes.get(j).getValue(),
                    equalTo(memberEntity.getAttributes().get(j).getValue()));
            assertThat(
                    attributes.get(j).getCreatedDate(),
                    equalTo(memberEntity.getAttributes().get(j).getCreatedDate()));
            assertThat(
                    attributes.get(j).getDefinition().getKey(),
                    equalTo(memberEntity.getAttributes().get(j).getDefinition().getKey()));
            assertThat(
                    attributes.get(j).getDefinition().getType(),
                    equalTo(memberEntity.getAttributes().get(j).getDefinition().getType()));
            assertThat(
                    attributes.get(j).getDefinition().getChoices(),
                    equalTo(memberEntity.getAttributes().get(j).getDefinition().getChoices()));
        }
        var subscription = member.getSubscription();
        for (int j = 0; j < subscription.size(); j++) {
            assertThat(
                    subscription.get(j).getYear(),
                    equalTo(memberEntity.getSubscription().get(j).getYear()));
            assertThat(
                    subscription.get(j).getPrice(),
                    equalTo(memberEntity.getSubscription().get(j).getPrice()));
            assertThat(
                    subscription.get(j).getAddedDate(),
                    equalTo(memberEntity.getSubscription().get(j).getAddedDate()));
        }
    }

    private void assertThatOrderMatchesEntity(Order order, OrderEntity orderEntity) {
        assertThat(order.getId(), equalTo(orderEntity.getId()));
        assertThat(order.getUuid(), equalTo(orderEntity.getUuid()));
        assertThat(order.getCreateDate(), equalTo(orderEntity.getCreateDate()));
        assertThat(order.getOwnerUserId(), equalTo(orderEntity.getOwnerUserId()));
        assertThat(order.getAccountingId(), equalTo(orderEntity.getAccountingId()));

        for (int i = 0; i < order.getMemberSubscription().size(); i++) {
            var subscription = order.getMemberSubscription().get(i);
            var subscriptionEntity = orderEntity.getMemberSubscription().get(i);

            assertThat(subscription.getAddedDate(), equalTo(subscriptionEntity.getAddedDate()));
            assertThat(subscription.getPrice(), equalTo(subscriptionEntity.getPrice()));
            assertThat(subscription.getYear(), equalTo(subscriptionEntity.getYear()));
        }

        for (int i = 0; i < order.getPayment().size(); i++) {
            var payment = order.getPayment().get(i);
            var paymentEntity = orderEntity.getPayment().get(i);

            assertThat(payment.getId(), equalTo(paymentEntity.getId()));
            assertThat(payment.getDate(), equalTo(paymentEntity.getDate()));
            assertThat(payment.getReference(), equalTo(paymentEntity.getReference()));
            assertThat(payment.getType(), equalTo(paymentEntity.getType()));
            assertThat(payment.getAmount(), equalTo(paymentEntity.getAmount()));
            assertThat(payment.getProcessingFees(), equalTo(paymentEntity.getProcessingFees()));
            assertThat(payment.getAccountingId(), equalTo(paymentEntity.getAccountingId()));
            assertThat(payment.getFeesAccountingId(), equalTo(paymentEntity.getFeesAccountingId()));
            assertThat(payment.getReconciled(), equalTo(paymentEntity.getReconciled()));
            assertThat(payment.getCollected(), equalTo(paymentEntity.getCollected()));
        }
    }
}
