package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.dto.AttributeDefinition;
import cricket.merstham.graphql.dto.Member;
import cricket.merstham.graphql.dto.MemberCategory;
import cricket.merstham.graphql.inputs.where.MemberCategoryWhereInput;
import cricket.merstham.graphql.services.MembershipService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

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
    public List<Member> memberSummary(@Argument int start, @Argument int length) {
        return membershipService.getMembers(start, length);
    }

    @QueryMapping
    public Member member(@Argument int id) {
        return membershipService.getMember(id);
    }

    @QueryMapping
    public List<MemberCategory> membershipCategories(@Argument MemberCategoryWhereInput where) {
        return membershipService.getCategories(where);
    }
}
