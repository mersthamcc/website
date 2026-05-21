package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.MembershipService;
import cricket.merstham.graphql.services.PassGeneratorService;
import cricket.merstham.graphql.services.PasskitUpdateService;
import cricket.merstham.shared.dto.Pass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
public class PassController {

    private final PassGeneratorService passGeneratorService;
    private final MembershipService membershipService;
    private final PasskitUpdateService passkitUpdateService;

    @Autowired
    public PassController(
            PassGeneratorService passGeneratorService,
            MembershipService membershipService,
            PasskitUpdateService passkitUpdateService) {
        this.passGeneratorService = passGeneratorService;
        this.membershipService = membershipService;
        this.passkitUpdateService = passkitUpdateService;
    }

    @QueryMapping
    public Pass pass(@Argument String memberUuid, @Argument String type, Principal principal)
            throws IOException {
        return passGeneratorService.getPassData(memberUuid, type, principal);
    }

    @MutationMapping
    public List<String> distributePasses(@Argument int memberId) {
        return membershipService.sendPasses(memberId);
    }
}
