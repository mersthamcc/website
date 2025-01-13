package cricket.merstham.graphql.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import cricket.merstham.graphql.entity.CouponEntity;
import cricket.merstham.graphql.entity.MemberAttributeEntity;
import cricket.merstham.graphql.entity.MemberAttributeEntityId;
import cricket.merstham.graphql.entity.MemberCategoryEntity;
import cricket.merstham.graphql.entity.MemberEntity;
import cricket.merstham.graphql.entity.MemberSubscriptionEntity;
import cricket.merstham.graphql.entity.MemberSubscriptionEntityId;
import cricket.merstham.graphql.entity.MemberSummaryEntity;
import cricket.merstham.graphql.entity.OrderEntity;
import cricket.merstham.graphql.entity.PaymentEntity;
import cricket.merstham.graphql.inputs.AttributeInput;
import cricket.merstham.graphql.inputs.MemberInput;
import cricket.merstham.graphql.inputs.PaymentInput;
import cricket.merstham.graphql.inputs.where.MemberCategoryWhereInput;
import cricket.merstham.graphql.repository.AttributeDefinitionEntityRepository;
import cricket.merstham.graphql.repository.CouponEntityRepository;
import cricket.merstham.graphql.repository.MemberCategoryEntityRepository;
import cricket.merstham.graphql.repository.MemberEntityRepository;
import cricket.merstham.graphql.repository.MemberFilterEntityRepository;
import cricket.merstham.graphql.repository.MemberSummaryRepository;
import cricket.merstham.graphql.repository.OrderEntityRepository;
import cricket.merstham.graphql.repository.PaymentEntityRepository;
import cricket.merstham.graphql.repository.PriceListItemEntityRepository;
import cricket.merstham.shared.dto.AttributeDefinition;
import cricket.merstham.shared.dto.Coupon;
import cricket.merstham.shared.dto.Member;
import cricket.merstham.shared.dto.MemberCategory;
import cricket.merstham.shared.dto.MemberFilter;
import cricket.merstham.shared.dto.MemberSummary;
import cricket.merstham.shared.dto.Order;
import cricket.merstham.shared.dto.Payment;
import cricket.merstham.shared.types.ReportFilter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static cricket.merstham.graphql.configuration.CacheConfiguration.MEMBER_COUNT_CACHE;
import static cricket.merstham.graphql.helpers.UserHelper.getSubject;
import static cricket.merstham.graphql.services.EmailService.MailTemplate.MEMBERSHIP_CONFIRM;
import static cricket.merstham.shared.IdentifierConstants.PLAYER_ID;
import static cricket.merstham.shared.types.ReportFilter.ALL;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
public class MembershipService {

    private final AttributeDefinitionEntityRepository attributeRepository;
    private final MemberEntityRepository memberRepository;
    private final MemberSummaryRepository summaryRepository;
    private final MemberCategoryEntityRepository memberCategoryEntityRepository;
    private final OrderEntityRepository orderEntityRepository;
    private final PaymentEntityRepository paymentEntityRepository;
    private final MemberFilterEntityRepository filterEntityRepository;
    private final PriceListItemEntityRepository priceListItemEntityRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final MemberSummaryRepository memberSummaryRepository;
    private final EmailService emailService;
    private final CognitoService cognitoService;
    private final CouponEntityRepository couponEntityRepository;

    @Autowired
    public MembershipService(
            AttributeDefinitionEntityRepository attributeRepository,
            MemberEntityRepository memberRepository,
            MemberSummaryRepository summaryRepository,
            MemberCategoryEntityRepository memberCategoryEntityRepository,
            OrderEntityRepository orderEntityRepository,
            PaymentEntityRepository paymentEntityRepository,
            MemberFilterEntityRepository filterEntityRepository,
            PriceListItemEntityRepository priceListItemEntityRepository,
            ModelMapper modelMapper,
            ObjectMapper objectMapper,
            MemberSummaryRepository memberSummaryRepository,
            EmailService emailService,
            CognitoService cognitoService,
            CouponEntityRepository couponEntityRepository) {
        this.attributeRepository = attributeRepository;
        this.memberRepository = memberRepository;
        this.summaryRepository = summaryRepository;
        this.memberCategoryEntityRepository = memberCategoryEntityRepository;
        this.orderEntityRepository = orderEntityRepository;
        this.paymentEntityRepository = paymentEntityRepository;
        this.filterEntityRepository = filterEntityRepository;
        this.priceListItemEntityRepository = priceListItemEntityRepository;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        this.memberSummaryRepository = memberSummaryRepository;
        this.emailService = emailService;
        this.cognitoService = cognitoService;
        this.couponEntityRepository = couponEntityRepository;
    }

    public List<AttributeDefinition> getAttributes() {
        return attributeRepository.findAll().stream()
                .map(a -> modelMapper.map(a, AttributeDefinition.class))
                .toList();
    }

    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public List<MemberSummary> getMembers(Principal principal) {
        return getMembers(principal, ALL);
    }

    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public List<MemberSummary> getMembers(Principal principal, ReportFilter reportFilter) {
        var filter = getUserFilter(principal);
        Specification<MemberSummaryEntity> baseSpecification =
                summaryRepository.getBaseSpecification(reportFilter);

        var members =
                filter.map(
                                f ->
                                        summaryRepository.findAll(
                                                baseSpecification.and(
                                                        summaryRepository.getMemberSpecification(
                                                                f))))
                        .orElse(summaryRepository.findAll(baseSpecification));
        return members.stream().map(m -> modelMapper.map(m, MemberSummary.class)).toList();
    }

    public List<MemberCategory> getCategories(MemberCategoryWhereInput where) {
        var categories = memberCategoryEntityRepository.findAll();
        return categories.stream()
                .filter(category -> isNull(where) || where.matches(category))
                .sorted(Comparator.comparing(MemberCategoryEntity::getSortOrder))
                .map(c -> modelMapper.map(c, MemberCategory.class))
                .toList();
    }

    @Cacheable(value = MEMBER_COUNT_CACHE)
    public long getMemberCount() {
        return memberSummaryRepository.count();
    }

    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public Member getMember(int id, Principal principal) {
        var filter = getUserFilter(principal);

        AtomicReference<Optional<MemberEntity>> member = new AtomicReference<>(Optional.empty());
        filter.ifPresentOrElse(
                f -> {
                    var results =
                            summaryRepository.findAll(
                                    summaryRepository.getMemberSpecificationWithId(f, id));
                    if (results.size() == 1) {
                        member.set(memberRepository.findById(id));
                    }
                },
                () -> member.set(memberRepository.findById(id)));
        return member.get().map(m -> modelMapper.map(m, Member.class)).orElseThrow();
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
    @Caching(
            evict = {
                @CacheEvict(value = MEMBER_COUNT_CACHE, allEntries = true),
            })
    public Member createMemberSubscription(MemberInput data, Principal principal) {
        if (nonNull(data.getMemberId())) {
            return updateMember(data.getMemberId(), data, principal);
        }

        var now = Instant.now();
        final var member =
                MemberEntity.builder()
                        .registrationDate(now)
                        .ownerUserId(getSubject(principal))
                        .uuid(UUID.randomUUID().toString())
                        .type("member")
                        .build();
        return saveMember(member, data, principal);
    }

    private Member updateMember(int id, MemberInput data, Principal principal) {
        final var member = memberRepository.findById(id).orElseThrow();
        return saveMember(member, data, principal);
    }

    private Member saveMember(MemberEntity member, MemberInput data, Principal principal) {
        var currentDate = LocalDate.ofInstant(member.getRegistrationDate(), ZoneId.systemDefault());
        var now = Instant.now();
        OrderEntity order =
                orderEntityRepository.findById(data.getSubscription().getOrderId()).orElseThrow();
        var priceListItem =
                priceListItemEntityRepository
                        .findById(data.getSubscription().getPriceListItemId())
                        .orElseThrow();

        if (isNull(member.getAttributes())) {
            member.setAttributes(new ArrayList<>());
        }

        data.getAttributes()
                .forEach(
                        a -> {
                            var attribute = member.getAttributeByName(a.getKey());
                            if (isNull(attribute)) {
                                attribute =
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
                                                .build();
                                member.getAttributes().add(attribute);
                            }
                            attribute.setUpdatedDate(now);
                            attribute.setValue(a.getValue());
                        });

        member.addSubscription(
                MemberSubscriptionEntity.builder()
                        .addedDate(currentDate)
                        .price(data.getSubscription().getPrice())
                        .pricelistItem(priceListItem)
                        .order(order)
                        .primaryKey(
                                MemberSubscriptionEntityId.builder()
                                        .member(member)
                                        .year(data.getSubscription().getYear())
                                        .build())
                        .build());
        return modelMapper.map(memberRepository.saveAndFlush(member), Member.class);
    }

    @PreAuthorize("isAuthenticated()")
    public Order createOrder(
            String uuid,
            BigDecimal total,
            BigDecimal discount,
            List<String> coupons,
            Principal principal) {
        var order =
                OrderEntity.builder()
                        .ownerUserId(getSubject(principal))
                        .uuid(uuid)
                        .total(total)
                        .discount(discount)
                        .createDate(LocalDate.now())
                        .build();
        order = orderEntityRepository.saveAndFlush(order);
        redeemCouponsAgainstOrder(coupons, order);

        return modelMapper.map(order, Order.class);
    }

    private void redeemCouponsAgainstOrder(List<String> coupons, OrderEntity order) {
        coupons.forEach(
                code -> {
                    var coupon = couponEntityRepository.findFirstByCode(code);
                    coupon.setRedeemDate(Instant.now());
                    coupon.setAppliedToOrderId(order.getId());
                    couponEntityRepository.saveAndFlush(coupon);
                });
    }

    @PreAuthorize("isAuthenticated()")
    public Order confirmOrder(int id, String paymentType, Principal principal) {
        var order = orderEntityRepository.findById(id).orElseThrow();
        var user = cognitoService.getUserDetails(getSubject(principal));
        if (!order.isConfirmed()) {
            emailService.sendEmail(
                    user.getEmail(),
                    MEMBERSHIP_CONFIRM,
                    Map.of(
                            "order", modelMapper.map(order, Order.class),
                            "user", user,
                            "paymentType", paymentType));

            order.setConfirmed(true);
            order = orderEntityRepository.saveAndFlush(order);
        }

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
                                .status(
                                        isNull(payment.getStatus())
                                                ? "pending"
                                                : payment.getStatus())
                                .build()),
                Payment.class);
    }

    @PreAuthorize("isAuthenticated()")
    @Caching(
            evict = {
                @CacheEvict(value = MEMBER_COUNT_CACHE, allEntries = true),
            })
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

    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public Member associateMemberToPlayer(int id, int playerId) {
        var member = memberRepository.findById(id).orElseThrow();
        member.getIdentifiers().put(PLAYER_ID, Long.toString(playerId));
        return modelMapper.map(memberRepository.saveAndFlush(member), Member.class);
    }

    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public Member deleteMemberToPlayerLink(int id) {
        var member = memberRepository.findById(id).orElseThrow();
        member.getIdentifiers().remove(PLAYER_ID);
        return modelMapper.map(memberRepository.saveAndFlush(member), Member.class);
    }

    private Optional<MemberFilter> getUserFilter(Principal principal) {
        return filterEntityRepository
                .findById(principal.getName())
                .map(f -> modelMapper.map(f, MemberFilter.class));
    }

    @PreAuthorize("isAuthenticated()")
    public List<MemberSummary> getMyMembers(Principal principal) {
        var members = summaryRepository.findAllByOwnerUserIdEquals(getSubject(principal));
        return members.stream().map(m -> modelMapper.map(m, MemberSummary.class)).toList();
    }

    @PreAuthorize("isAuthenticated()")
    public List<Member> getMyMemberDetails(Principal principal) {
        return memberRepository.findAllByOwnerUserId(getSubject(principal)).stream()
                .map(m -> modelMapper.map(m, Member.class))
                .toList();
    }

    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public List<MemberSummary> getMembersForUser(String owner) {
        var members = summaryRepository.findAllByOwnerUserIdEquals(owner);
        return members.stream().map(m -> modelMapper.map(m, MemberSummary.class)).toList();
    }

    @PreAuthorize("isAuthenticated()")
    public Member addMemberIdentifier(int id, String name, String value, Principal principal) {
        var member =
                memberRepository.findByIdAndOwnerUserId(id, getSubject(principal)).orElseThrow();
        member.getIdentifiers().put(name, value);
        return modelMapper.map(memberRepository.saveAndFlush(member), Member.class);
    }

    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public Coupon saveCoupon(Coupon coupon, Principal principal) {
        var entity = couponEntityRepository.findById(coupon.getId()).orElseGet(CouponEntity::new);
        modelMapper.map(coupon, entity);

        return modelMapper.map(couponEntityRepository.saveAndFlush(entity), Coupon.class);
    }

    @PreAuthorize("isAuthenticated()")
    public List<Coupon> getMyCoupons(Principal principal) {
        return couponEntityRepository.findAllByOwnerUserId(getSubject(principal)).stream()
                .map((element) -> modelMapper.map(element, Coupon.class))
                .toList();
    }
}
