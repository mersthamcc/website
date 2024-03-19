package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.shared.dto.MemberSummary;
import cricket.merstham.website.frontend.exception.GraphException;
import cricket.merstham.website.frontend.model.DataTableColumn;
import cricket.merstham.website.frontend.model.DataTableValue;
import cricket.merstham.website.frontend.model.datatables.SspRequest;
import cricket.merstham.website.frontend.model.datatables.SspResponse;
import cricket.merstham.website.frontend.model.datatables.SspResponseDataWrapper;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.MembershipService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cricket.merstham.website.frontend.configuration.CacheConfiguration.MEMBER_SUMMARY_CACHE;
import static cricket.merstham.website.frontend.helpers.AttributeConverter.convert;
import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_MEMBER_EDIT_ROUTE;
import static java.lang.Math.min;
import static java.text.MessageFormat.format;
import static java.util.Objects.nonNull;

@Controller("AdminMembershipController")
public class MembershipController extends SspController<MemberSummary> {

    private static final Logger LOG = LogManager.getLogger(MembershipController.class);
    private final MessageSource messageSource;

    private final MembershipService membershipService;

    @Autowired
    public MembershipController(MessageSource messageSource, MembershipService membershipService) {
        this.messageSource = messageSource;
        this.membershipService = membershipService;
    }

    @GetMapping(value = "/administration/membership", name = "admin-membership-list")
    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    @CacheEvict(
            value = MEMBER_SUMMARY_CACHE,
            key = "#cognitoAuthentication.OAuth2AccessToken.tokenValue")
    public ModelAndView list(CognitoAuthentication cognitoAuthentication) {
        return new ModelAndView(
                "administration/membership/list",
                Map.of(
                        "memberColumns",
                        List.of(
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
                                        .setFieldName("mostRecentSubscription"))));
    }

    @GetMapping(value = "/administration/membership/edit/{id}", name = "admin-membership-edit")
    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public ModelAndView edit(
            CognitoAuthentication cognitoAuthentication, Locale locale, @PathVariable int id) {
        var member =
                membershipService
                        .get(id, cognitoAuthentication.getOAuth2AccessToken())
                        .orElseThrow();
        return new ModelAndView("administration/membership/edit", buildModelData(member, locale));
    }

    private Map<String, ?> buildModelData(
            cricket.merstham.shared.dto.Member member, Locale locale) {
        return Map.of(
                "member", member,
                "subscription", member.getSubscription().get(0),
                "data",
                        member.getAttributes().stream()
                                .collect(
                                        Collectors.toMap(
                                                a -> a.getDefinition().getKey(),
                                                a ->
                                                        convert(
                                                                a.getDefinition(),
                                                                a.getValue(),
                                                                locale))),
                "subscriptionHistory",
                        member.getSubscription().stream()
                                .map(
                                        s ->
                                                Map.of(
                                                        "membership.year",
                                                                new DataTableValue()
                                                                        .setValue(
                                                                                Integer.toString(
                                                                                        s
                                                                                                .getYear())),
                                                        "membership.description",
                                                                new DataTableValue()
                                                                        .setValue(
                                                                                s.getPriceListItem()
                                                                                        .getDescription()),
                                                        "membership.category",
                                                                new DataTableValue()
                                                                        .setValue(
                                                                                messageSource
                                                                                        .getMessage(
                                                                                                format(
                                                                                                        "membership.{0}",
                                                                                                        s.getPriceListItem()
                                                                                                                .getMemberCategory()
                                                                                                                .getKey()),
                                                                                                null,
                                                                                                locale)),
                                                        "membership.price",
                                                                new DataTableValue()
                                                                        .setValue(
                                                                                NumberFormat
                                                                                        .getCurrencyInstance()
                                                                                        .format(
                                                                                                s
                                                                                                        .getPrice()))))
                                .toList(),
                "subscriptionHistoryColumns",
                        List.of(
                                new DataTableColumn().setKey("membership.year"),
                                new DataTableColumn().setKey("membership.description"),
                                new DataTableColumn().setKey("membership.category"),
                                new DataTableColumn().setKey("membership.price")),
                "payments",
                        member.getSubscription().get(0).getOrder().getPayment().stream()
                                .map(
                                        p ->
                                                Map.of(
                                                        "payments.date",
                                                                new DataTableValue()
                                                                        .setValue(
                                                                                p.getDate()
                                                                                        .format(
                                                                                                DateTimeFormatter
                                                                                                        .ofLocalizedDate(
                                                                                                                FormatStyle
                                                                                                                        .SHORT))),
                                                        "payments.type",
                                                                new DataTableValue()
                                                                        .setValue(
                                                                                messageSource
                                                                                        .getMessage(
                                                                                                format(
                                                                                                        "payments.{0}-short",
                                                                                                        p
                                                                                                                .getType()),
                                                                                                null,
                                                                                                locale)),
                                                        "payments.reference",
                                                                new DataTableValue()
                                                                        .setValue(p.getReference()),
                                                        "payments.collected",
                                                                new DataTableValue()
                                                                        .setValue(
                                                                                p.getCollected()
                                                                                        ? "Yes"
                                                                                        : "No"),
                                                        "payments.amount",
                                                                new DataTableValue()
                                                                        .setValue(
                                                                                NumberFormat
                                                                                        .getCurrencyInstance()
                                                                                        .format(
                                                                                                p
                                                                                                        .getAmount()))))
                                .toList(),
                "paymentsColumns",
                        List.of(
                                new DataTableColumn().setKey("payments.date"),
                                new DataTableColumn().setKey("payments.type"),
                                new DataTableColumn().setKey("payments.reference"),
                                new DataTableColumn().setKey("payments.collected"),
                                new DataTableColumn().setKey("payments.amount")));
    }

    @PostMapping(value = "/administration/membership/edit/{id}", name = "admin-membership-update")
    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public RedirectView update(
            CognitoAuthentication cognitoAuthentication,
            Locale locale,
            RedirectAttributes redirectAttributes,
            @PathVariable int id,
            @RequestBody MultiValueMap<String, Object> data) {
        try {
            membershipService.update(
                    id, cognitoAuthentication.getOAuth2AccessToken(), locale, data);
        } catch (GraphException ex) {
            LOG.error("Error performing update!", ex);
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return redirectTo(format("/administration/membership/edit/{0}", id));
    }

    @PostMapping(
            value = "/administration/membership/edit/{id}/play-cricket-link",
            name = "admin-membership-play-cricket-link")
    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public RedirectView playCricketLink(
            CognitoAuthentication cognitoAuthentication,
            RedirectAttributes redirectAttributes,
            @PathVariable int id,
            @RequestBody MultiValueMap<String, Object> data) {
        try {
            var playCricketId = Integer.parseInt((String) data.getFirst("play-cricket-id"));
            membershipService.linkToPlayCricketPlayer(
                    id, cognitoAuthentication.getOAuth2AccessToken(), playCricketId);
        } catch (GraphException ex) {
            LOG.error("Error performing update!", ex);
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return redirectTo(format("/administration/membership/edit/{0}", id));
    }

    @PostMapping(
            consumes = "application/json",
            produces = "application/json",
            path = "/administration/membership/get-data")
    public @ResponseBody SspResponse<SspResponseDataWrapper<MemberSummary>> getData(
            CognitoAuthentication cognitoAuthentication, @RequestBody SspRequest request) {
        Comparator<MemberSummary> comparator = createComparator(request);
        String search = request.getSearch().getValue().toLowerCase();
        List<MemberSummary> members =
                membershipService
                        .getMemberSummary(cognitoAuthentication.getOAuth2AccessToken())
                        .stream()
                        .filter(
                                m ->
                                        m.getFamilyName().toLowerCase().contains(search)
                                                || m.getGivenName().toLowerCase().contains(search)
                                                || m.getLastSubsCategory()
                                                        .toLowerCase()
                                                        .contains(search)
                                                || m.getLastSubsYear().contains(search)
                                                || (nonNull(m.getAgeGroup())
                                                        && m.getAgeGroup().equalsIgnoreCase(search))
                                                || (nonNull(m.getGender())
                                                        && m.getGender().equalsIgnoreCase(search)))
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

    private Comparator<MemberSummary> createComparator(SspRequest request) {
        return request.getOrder().stream()
                .findFirst()
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
