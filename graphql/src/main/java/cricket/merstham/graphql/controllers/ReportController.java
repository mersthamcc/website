package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.GoogleSheetsService;
import cricket.merstham.graphql.services.MembershipService;
import cricket.merstham.shared.dto.ReportExport;
import cricket.merstham.shared.types.ReportFilter;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ReportController {

    private final MembershipService membershipService;
    private final GoogleSheetsService googleSheetsService;

    public ReportController(
            MembershipService membershipService, GoogleSheetsService googleSheetsService) {
        this.membershipService = membershipService;
        this.googleSheetsService = googleSheetsService;
    }

    @QueryMapping
    public ReportExport exportFilteredMembers(Principal principal, @Argument ReportFilter filter) {
        var members = membershipService.getMembers(principal, filter);

        return googleSheetsService.exportMemberSummary(principal, members, filter);
    }
}
