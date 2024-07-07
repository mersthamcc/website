package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.inputs.AttributeInput;
import cricket.merstham.graphql.inputs.MemberInput;
import cricket.merstham.graphql.inputs.PaymentInput;
import cricket.merstham.graphql.inputs.where.MemberCategoryWhereInput;
import cricket.merstham.graphql.services.MembershipService;
import cricket.merstham.shared.dto.AttributeDefinition;
import cricket.merstham.shared.dto.Member;
import cricket.merstham.shared.dto.MemberCategory;
import cricket.merstham.shared.dto.MemberSummary;
import cricket.merstham.shared.dto.Order;
import cricket.merstham.shared.dto.Payment;
import cricket.merstham.shared.types.ReportFilter;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @QueryMapping
    public List<AttributeDefinition> attributes() {
        return membershipService.getAttributes();
    }

    @QueryMapping
    public List<MemberSummary> members(Principal principal) {
        return membershipService.getMembers(principal);
    }

    @QueryMapping
    public List<MemberSummary> filteredMembers(Principal principal, @Argument ReportFilter filter) {
        return membershipService.getMembers(principal, filter);
    }

    @QueryMapping
    public List<MemberSummary> myMembers(Principal principal) {
        return membershipService.getMyMembers(principal);
    }

    @QueryMapping
    public List<MemberSummary> membersOwnedBy(@Argument String owner) {
        return membershipService.getMembersForUser(owner);
    }

    @QueryMapping
    public Member member(@Argument int id, Principal principal) {
        return membershipService.getMember(id, principal);
    }

    @QueryMapping
    public List<MemberCategory> membershipCategories(@Argument MemberCategoryWhereInput where) {
        var result = membershipService.getCategories(where);
        return result;
    }

    @QueryMapping
    public List<Order> orders(@Argument int year) {
        return membershipService.getOrders(year);
    }

    @QueryMapping
    public List<Order> myOrders(Principal principal) {
        return membershipService.getMyOrders(principal);
    }

    @QueryMapping
    public Order order(@Argument int id) {
        return membershipService.getOrder(id);
    }

    @MutationMapping
    public Member createMember(@Argument MemberInput data, Principal principal) {
        return membershipService.createMember(data, principal);
    }

    @MutationMapping
    public Order createOrder(
            @Argument String uuid,
            @Argument BigDecimal total,
            @Argument BigDecimal discount,
            Principal principal) {
        return membershipService.createOrder(uuid, total, discount, principal);
    }

    @MutationMapping
    public Payment addPaymentToOrder(
            @Argument int orderId, @Argument PaymentInput payment, Principal principal) {
        return membershipService.addPaymentToOrder(orderId, payment, principal);
    }

    @MutationMapping
    public Member updateMember(
            @Argument int id, @Argument List<AttributeInput> data, Principal principal) {
        return membershipService.updateMember(id, data);
    }

    @MutationMapping
    public Member associateMemberToPlayer(@Argument int id, @Argument int playerId) {
        return membershipService.associateMemberToPlayer(id, playerId);
    }

    @MutationMapping
    public Member deleteMemberToPlayerLink(@Argument int id) {
        return membershipService.deleteMemberToPlayerLink(id);
    }

    @MutationMapping
    public Member addMemberIdentifier(
            @Argument int id, @Argument String name, @Argument String value, Principal principal) {
        return membershipService.addMemberIdentifier(id, name, value, principal);
    }
}
