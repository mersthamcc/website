package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.shared.dto.MemberSummary;
import cricket.merstham.website.frontend.model.DataTableColumn;
import cricket.merstham.website.frontend.model.datatables.SspRequest;
import cricket.merstham.website.frontend.model.datatables.SspResponse;
import cricket.merstham.website.frontend.model.datatables.SspResponseDataWrapper;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.MembershipService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_MEMBER_EDIT_ROUTE;
import static java.lang.Math.min;
import static java.util.Objects.nonNull;

@Controller("AdminMembershipReportController")
public class MembershipReportsController extends SspController<MemberSummary> {

    private static final Logger LOG = LogManager.getLogger(MembershipReportsController.class);
    private final MessageSource messageSource;

    private final MembershipService membershipService;

    @Autowired
    public MembershipReportsController(
            MessageSource messageSource,
            MembershipService membershipService) {
        this.messageSource = messageSource;
        this.membershipService = membershipService;
    }

    @GetMapping(value = "/administration/membership-report/{report}", name = "admin-membership-report")
    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public ModelAndView reports(@PathVariable String report, CognitoAuthentication cognitoAuthentication) {
        return new ModelAndView(
                "administration/membership-report/report",
                Map.of(
                        "memberColumns",
                        List.of(
                                new DataTableColumn()
                                        .setKey("membership.play-cricket.table-icon")
                                        .setFunction(true)
                                        .setSortable(false)
                                        .setFunctionName("playCricketLink"),
                                new DataTableColumn()
                                        .setKey("membership.family-name")
                                        .setFieldName("familyName"),
                                new DataTableColumn()
                                        .setKey("membership.given-name")
                                        .setFieldName("givenName"),
                                new DataTableColumn()
                                        .setKey("membership.category")
                                        .setFieldName("lastSubsCategory"),
                                new DataTableColumn()
                                        .setKey("membership.age-group")
                                        .setFieldName("ageGroup"),
                                new DataTableColumn()
                                        .setKey("membership.gender")
                                        .setFieldName("gender"),
                                new DataTableColumn()
                                        .setKey("membership.last-subscription")
                                        .setFieldName("mostRecentSubscription"),
                                new DataTableColumn()
                                        .setKey("membership.status")
                                        .setFunction(true)
                                        .setFunctionName("unpaid")
                                        .setSortable(false),
                                new DataTableColumn()
                                        .setKey("membership.tags")
                                        .setFunction(true)
                                        .setFunctionName("tags")
                                        .setSortable(false))));
    }

    @PostMapping(
            consumes = "application/json",
            produces = "application/json",
            path = "/administration/membership-report/get-data")
    public @ResponseBody SspResponse<SspResponseDataWrapper<MemberSummary>> getData(
            CognitoAuthentication cognitoAuthentication, @RequestBody SspRequest request) {
        Comparator<MemberSummary> comparator = createComparator(request);
        String search = request.getSearch().getValue().toLowerCase();
        List<MemberSummary> members =
                membershipService
                        .getMemberSummary(cognitoAuthentication.getOAuth2AccessToken())
                        .stream()
                        .filter(m -> matchesCriteria(m, search))
                        .sorted(comparator)
                        .toList();
        return SspResponse.<SspResponseDataWrapper<MemberSummary>>builder()
                .draw(request.getDraw())
                .data(
                        members
                                .subList(
                                        request.getStart(),
                                        min(
                                                request.getStart() + request.getLength(),
                                                members.size()))
                                .stream()
                                .map(
                                        m ->
                                                SspResponseDataWrapper.<MemberSummary>builder()
                                                        .data(m)
                                                        .editRouteTemplate(
                                                                Optional.of(
                                                                        ADMIN_MEMBER_EDIT_ROUTE))
                                                        .deleteRouteTemplate(Optional.empty())
                                                        .mapFunction(
                                                                member ->
                                                                        Map.of(
                                                                                "id",
                                                                                member.getId()))
                                                        .build())
                                .toList())
                .recordsTotal(members.size())
                .recordsFiltered(members.size())
                .build();
    }

    private boolean matchesCriteria(MemberSummary member, String search) {
        return Arrays.stream(search.split(" "))
                .map(
                        s ->
                                member.getFamilyName().toLowerCase().contains(s)
                                        || member.getGivenName().toLowerCase().contains(s)
                                        || member.getLastSubsCategory().toLowerCase().contains(s)
                                        || member.getLastSubsYear().contains(s)
                                        || (nonNull(member.getAgeGroup())
                                                && member.getAgeGroup().equalsIgnoreCase(s))
                                        || (nonNull(member.getGender())
                                                && member.getGender().equalsIgnoreCase(s)))
                .allMatch(Boolean::booleanValue);
    }

    private Comparator<MemberSummary> createComparator(SspRequest request) {
        return request.getOrder().stream()
                .findFirst()
                .filter(f -> !request.getColumns().get(f.getColumn()).getData().equals("function"))
                .map(
                        c -> {
                            String column = request.getColumns().get(c.getColumn()).getData();
                            Comparator<MemberSummary> comparator = null;
                            switch (column) {
                                case "data.givenName":
                                    comparator =
                                            Comparator.comparing(
                                                    MemberSummary::getGivenName,
                                                    Comparator.nullsFirst(
                                                            Comparator.naturalOrder()));
                                    break;
                                case "data.familyName":
                                    comparator =
                                            Comparator.comparing(
                                                    MemberSummary::getFamilyName,
                                                    Comparator.nullsFirst(
                                                            Comparator.naturalOrder()));
                                    break;
                                case "data.lastSubsCategory":
                                    comparator =
                                            Comparator.comparing(
                                                    MemberSummary::getLastSubsCategory,
                                                    Comparator.nullsFirst(
                                                            Comparator.naturalOrder()));
                                    break;
                                case "data.ageGroup":
                                    comparator =
                                            Comparator.comparing(
                                                    MemberSummary::getAgeGroup,
                                                    Comparator.nullsFirst(
                                                            Comparator.naturalOrder()));
                                    break;
                                case "data.gender":
                                    comparator =
                                            Comparator.comparing(
                                                    MemberSummary::getGender,
                                                    Comparator.nullsFirst(
                                                            Comparator.naturalOrder()));
                                    break;
                                case "data.mostRecentSubscription":
                                    comparator =
                                            Comparator.comparing(
                                                    MemberSummary::getLastSubsDate,
                                                    Comparator.nullsFirst(
                                                            Comparator.naturalOrder()));
                                    break;
                            }
                            if ("desc".equals(c.getDir())) {
                                comparator = comparator.reversed();
                            }
                            return comparator;
                        })
                .orElse(Comparator.comparing(MemberSummary::getFamilyName));
    }
}
