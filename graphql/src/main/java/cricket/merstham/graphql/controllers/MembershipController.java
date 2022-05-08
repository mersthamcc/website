package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.dto.AttributeDefinition;
import cricket.merstham.graphql.dto.Member;
import cricket.merstham.graphql.dto.MemberCategory;
import cricket.merstham.graphql.dto.Order;
import cricket.merstham.graphql.dto.Payment;
import cricket.merstham.graphql.inputs.MemberInput;
import cricket.merstham.graphql.inputs.PaymentInput;
import cricket.merstham.graphql.inputs.where.MemberCategoryWhereInput;
import cricket.merstham.graphql.services.MembershipService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

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
    public List<Member> members() {
        return membershipService.getMembers();
    }

    @QueryMapping
    public Member member(@Argument int id) {
        return membershipService.getMember(id);
    }

    @QueryMapping
    public List<MemberCategory> membershipCategories(@Argument MemberCategoryWhereInput where) {
        return membershipService.getCategories(where);
    }

    @QueryMapping
    public List<Order> orders(@Argument int year) {
        return membershipService.getOrders(year);
    }

    @QueryMapping
    public List<Order> myOrders(Principal principal) {
        return membershipService.getMyOrders(principal);
    }

    @MutationMapping
    public Member createMember(@Argument MemberInput data, Principal principal) {
        return membershipService.createMember(data, principal);
    }

    @MutationMapping
    public Order createOrder(@Argument String uuid, Principal principal) {
        return membershipService.createOrder(uuid, principal);
    }

    @MutationMapping
    public Payment addPaymentToOrder(
            @Argument int orderId, @Argument PaymentInput payment, Principal principal) {
        return membershipService.addPaymentToOrder(orderId, payment, principal);
    }

    //    updateMember(id: Int!, data: [AttributeInput]!): Member

}
