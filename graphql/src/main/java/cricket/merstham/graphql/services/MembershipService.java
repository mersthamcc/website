package cricket.merstham.graphql.services;

import cricket.merstham.graphql.entity.MemberAttributeEntity;
import cricket.merstham.graphql.entity.MemberAttributeEntityId;
import cricket.merstham.graphql.entity.MemberCategoryEntity;
import cricket.merstham.graphql.entity.MemberEntity;
import cricket.merstham.graphql.entity.MemberSubscriptionEntity;
import cricket.merstham.graphql.entity.MemberSubscriptionEntityId;
import cricket.merstham.graphql.entity.OrderEntity;
import cricket.merstham.graphql.entity.PaymentEntity;
import cricket.merstham.graphql.inputs.AttributeInput;
import cricket.merstham.graphql.inputs.MemberInput;
import cricket.merstham.graphql.inputs.PaymentInput;
import cricket.merstham.graphql.inputs.where.MemberCategoryWhereInput;
import cricket.merstham.graphql.repository.AttributeDefinitionEntityRepository;
import cricket.merstham.graphql.repository.MemberCategoryEntityRepository;
import cricket.merstham.graphql.repository.MemberEntityRepository;
import cricket.merstham.graphql.repository.OrderEntityRepository;
import cricket.merstham.graphql.repository.PaymentEntityRepository;
import cricket.merstham.graphql.repository.PriceListItemEntityRepository;
import cricket.merstham.shared.dto.AttributeDefinition;
import cricket.merstham.shared.dto.Member;
import cricket.merstham.shared.dto.MemberCategory;
import cricket.merstham.shared.dto.Order;
import cricket.merstham.shared.dto.Payment;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

import static cricket.merstham.graphql.helpers.UserHelper.getSubject;
import static java.util.Objects.isNull;

@Component
public class MembershipService {

    private final AttributeDefinitionEntityRepository attributeRepository;
    private final MemberEntityRepository memberRepository;
    private final MemberCategoryEntityRepository memberCategoryEntityRepository;
    private final OrderEntityRepository orderEntityRepository;
    private final PaymentEntityRepository paymentEntityRepository;

    private final PriceListItemEntityRepository priceListItemEntityRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public MembershipService(
            AttributeDefinitionEntityRepository attributeRepository,
            MemberEntityRepository memberRepository,
            MemberCategoryEntityRepository memberCategoryEntityRepository,
            OrderEntityRepository orderEntityRepository,
            PaymentEntityRepository paymentEntityRepository,
            PriceListItemEntityRepository priceListItemEntityRepository,
            ModelMapper modelMapper) {
        this.attributeRepository = attributeRepository;
        this.memberRepository = memberRepository;
        this.memberCategoryEntityRepository = memberCategoryEntityRepository;
        this.orderEntityRepository = orderEntityRepository;
        this.paymentEntityRepository = paymentEntityRepository;
        this.priceListItemEntityRepository = priceListItemEntityRepository;
        this.modelMapper = modelMapper;
    }

    public List<AttributeDefinition> getAttributes() {
        return attributeRepository.findAll().stream()
                .map(a -> modelMapper.map(a, AttributeDefinition.class))
                .toList();
    }

    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public List<Member> getMembers() {
        var members = memberRepository.findAllByCancelledIsNull();
        return members.stream().map(m -> modelMapper.map(m, Member.class)).toList();
    }

    public List<MemberCategory> getCategories(MemberCategoryWhereInput where) {
        var categories = memberCategoryEntityRepository.findAll();
        return categories.stream()
                .filter(category -> isNull(where) || where.matches(category))
                .sorted(Comparator.comparing(MemberCategoryEntity::getId))
                .map(c -> modelMapper.map(c, MemberCategory.class))
                .toList();
    }

    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public Member getMember(int id) {
        var member = memberRepository.findById(id);
        return member.map(m -> modelMapper.map(m, Member.class)).orElseThrow();
    }

    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public List<Order> getOrders(int year) {
        return orderEntityRepository
                .findByCreateDateBetween(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31))
                .stream()
                .map(o -> modelMapper.map(o, Order.class))
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    public List<Order> getMyOrders(Principal principal) {
        return orderEntityRepository
                .findByOwnerUserIdAllIgnoreCaseOrderByCreateDateAsc(getSubject(principal))
                .stream()
                .map(o -> modelMapper.map(o, Order.class))
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    public Order getOrder(int id) {
        return modelMapper.map(orderEntityRepository.findById(id), Order.class);
    }

    @PreAuthorize("isAuthenticated()")
    public Member createMember(MemberInput data, Principal principal) {
        var now = Instant.now();
        var currentDate = LocalDate.ofInstant(now, ZoneId.systemDefault());
        OrderEntity order =
                orderEntityRepository.findById(data.getSubscription().getOrderId()).orElseThrow();
        var priceListItem =
                priceListItemEntityRepository
                        .findById(data.getSubscription().getPriceListItemId())
                        .orElseThrow();
        final var member =
                MemberEntity.builder()
                        .registrationDate(now)
                        .ownerUserId(getSubject(principal))
                        .type("member")
                        .build();

        member.setAttributes(
                data.getAttributes().stream()
                        .map(
                                a ->
                                        MemberAttributeEntity.builder()
                                                .primaryKey(
                                                        MemberAttributeEntityId.builder()
                                                                .member(member)
                                                                .definition(
                                                                        attributeRepository
                                                                                .findByKey(
                                                                                        a.getKey()))
                                                                .build())
                                                .createdDate(now)
                                                .updatedDate(now)
                                                .value(a.getValue())
                                                .build())
                        .toList());

        member.setSubscription(
                List.of(
                        MemberSubscriptionEntity.builder()
                                .addedDate(currentDate)
                                .price(data.getSubscription().getPrice())
                                .pricelistItem(priceListItem)
                                .order(order)
                                .primaryKey(
                                        MemberSubscriptionEntityId.builder()
                                                .member(member)
                                                .year(currentDate.getYear())
                                                .build())
                                .build()));
        return modelMapper.map(memberRepository.saveAndFlush(member), Member.class);
    }

    @PreAuthorize("isAuthenticated()")
    public Order createOrder(String uuid, Principal principal) {
        var order =
                OrderEntity.builder()
                        .ownerUserId(getSubject(principal))
                        .uuid(uuid)
                        .createDate(LocalDate.now())
                        .build();
        order = orderEntityRepository.saveAndFlush(order);
        return modelMapper.map(order, Order.class);
    }

    @PreAuthorize("isAuthenticated()")
    public Payment addPaymentToOrder(int orderId, PaymentInput payment, Principal principal) {
        OrderEntity order = orderEntityRepository.findById(orderId).orElseThrow();
        return modelMapper.map(
                paymentEntityRepository.saveAndFlush(
                        PaymentEntity.builder()
                                .type(payment.getType())
                                .reference(payment.getReference())
                                .amount(payment.getAmount())
                                .processingFees(payment.getProcessingFees())
                                .date(payment.getDate())
                                .collected(payment.isCollected())
                                .reconciled(payment.isReconciled())
                                .order(order)
                                .build()),
                Payment.class);
    }

    @PreAuthorize("isAuthenticated()")
    public Member updateMember(int id, List<AttributeInput> attributes) {
        var member = memberRepository.findById(id).orElseThrow();
        attributes.forEach(
                input ->
                        member.getAttributes().stream()
                                .filter(a -> a.getDefinition().getKey().equals(input.getKey()))
                                .findFirst()
                                .ifPresentOrElse(
                                        a -> {
                                            a.setValue(input.getValue());
                                            a.setUpdatedDate(Instant.now());
                                        },
                                        () -> {
                                            var a =
                                                    MemberAttributeEntity.builder()
                                                            .createdDate(Instant.now())
                                                            .updatedDate(Instant.now())
                                                            .value(input.getValue())
                                                            .primaryKey(
                                                                    MemberAttributeEntityId
                                                                            .builder()
                                                                            .definition(
                                                                                    attributeRepository
                                                                                            .findByKey(
                                                                                                    input
                                                                                                            .getKey()))
                                                                            .build())
                                                            .build();
                                            member.getAttributes().add(a);
                                        }));

        return modelMapper.map(memberRepository.saveAndFlush(member), Member.class);
    }
}
