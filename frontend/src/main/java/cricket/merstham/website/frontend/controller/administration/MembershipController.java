package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.shared.dto.Member;
import cricket.merstham.shared.dto.MemberSummary;
import cricket.merstham.website.frontend.exception.GraphException;
import cricket.merstham.website.frontend.model.DataTableColumn;
import cricket.merstham.website.frontend.model.DataTableValue;
import cricket.merstham.website.frontend.model.datatables.SspRequest;
import cricket.merstham.website.frontend.model.datatables.SspResponse;
import cricket.merstham.website.frontend.model.datatables.SspResponseDataWrapper;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.MembershipService;
import cricket.merstham.website.frontend.service.PlayerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
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
    private final PlayerService playerService;

    @Autowired
    public MembershipController(
            MessageSource messageSource,
            MembershipService membershipService,
            PlayerService playerService) {
        this.messageSource = messageSource;
        this.membershipService = membershipService;
        this.playerService = playerService;
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

    @GetMapping(value = "/administration/membership/edit/{id}", name = "admin-membership-edit")
    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public ModelAndView edit(
            CognitoAuthentication cognitoAuthentication, Locale locale, @PathVariable int id) {
        var member =
                membershipService
                        .get(id, cognitoAuthentication.getOAuth2AccessToken())
                        .orElseThrow();
        return new ModelAndView(
                "administration/membership/edit",
                buildModelData(member, locale, cognitoAuthentication.getOAuth2AccessToken()));
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

    @GetMapping(
            value = "/administration/membership/edit/{id}/delete-play-cricket-link",
            name = "admin-membership-delete-play-cricket-link")
    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public RedirectView deletePlayCricketLink(
            CognitoAuthentication cognitoAuthentication,
            RedirectAttributes redirectAttributes,
            @PathVariable int id) {
        try {
            membershipService.deletePlayCricketLink(
                    id, cognitoAuthentication.getOAuth2AccessToken());
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

    private Map<String, ?> buildModelData(
            Member member, Locale locale, OAuth2AccessToken accessToken) {
        var model = new HashMap<String, Object>();
        model.put("member", member);
        model.put("subscription", member.getSubscription().get(0));
        model.put(
                "data",
                member.getAttributes().stream()
                        .collect(
                                Collectors.toMap(
                                        a -> a.getDefinition().getKey(),
                                        a -> convert(a.getDefinition(), a.getValue(), locale))));
        model.put(
                "subscriptionHistory",
                member.getSubscription().stream()
                        .map(
                                s ->
                                        Map.of(
                                                "membership.year",
                                                new DataTableValue()
                                                        .setValue(Integer.toString(s.getYear())),
                                                "membership.description",
                                                new DataTableValue()
                                                        .setValue(
                                                                s.getPriceListItem()
                                                                        .getDescription()),
                                                "membership.category",
                                                new DataTableValue()
                                                        .setValue(
                                                                messageSource.getMessage(
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
                                                                NumberFormat.getCurrencyInstance()
                                                                        .format(s.getPrice())),
                                                "membership.order",
                                                new DataTableValue()
                                                        .setValue(s.getOrder().getWebReference())))
                        .toList());
        model.put(
                "subscriptionHistoryColumns",
                List.of(
                        new DataTableColumn().setKey("membership.year"),
                        new DataTableColumn().setKey("membership.description"),
                        new DataTableColumn().setKey("membership.category"),
                        new DataTableColumn().setKey("membership.price"),
                        new DataTableColumn().setKey("membership.order")));
        model.put(
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
                                                                messageSource.getMessage(
                                                                        format(
                                                                                "payments.{0}-short",
                                                                                p.getType()),
                                                                        null,
                                                                        locale)),
                                                "payments.reference",
                                                new DataTableValue().setValue(p.getReference()),
                                                "payments.collected",
                                                new DataTableValue()
                                                        .setValue(p.getCollected() ? "Yes" : "No"),
                                                "payments.amount",
                                                new DataTableValue()
                                                        .setValue(
                                                                NumberFormat.getCurrencyInstance()
                                                                        .format(p.getAmount()))))
                        .toList());
        model.put(
                "paymentsColumns",
                List.of(
                        new DataTableColumn().setKey("payments.date"),
                        new DataTableColumn().setKey("payments.type"),
                        new DataTableColumn().setKey("payments.reference"),
                        new DataTableColumn().setKey("payments.collected"),
                        new DataTableColumn().setKey("payments.amount")));

        if (nonNull(member.getPlayerId())) {
            var id = Integer.parseInt(member.getPlayerId());
            var player = playerService.getPlayer(id, accessToken);
            if (nonNull(player)) model.put("player", player);
        }
        return model;
    }
}
