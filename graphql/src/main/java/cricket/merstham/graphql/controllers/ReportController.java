package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.inputs.AttendanceFilterInput;
import cricket.merstham.graphql.services.GoogleSheetsService;
import cricket.merstham.graphql.services.MembershipService;
import cricket.merstham.shared.dto.MemberAttendanceSummary;
import cricket.merstham.shared.dto.ReportExport;
import cricket.merstham.shared.types.ReportFilter;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

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

    @QueryMapping
    public List<MemberAttendanceSummary> attendances(
            Principal principal, @Argument AttendanceFilterInput filter) {
        return membershipService.getMemberAttedance(principal, filter);
    }
}
