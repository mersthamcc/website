package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.website.frontend.model.DataTableColumn;
import cricket.merstham.website.frontend.model.DataTableValue;
import cricket.merstham.website.frontend.model.GenericForm;
import cricket.merstham.website.frontend.model.admintables.Member;
import cricket.merstham.website.frontend.model.datatables.SspRequest;
import cricket.merstham.website.frontend.model.datatables.SspResponse;
import cricket.merstham.website.frontend.service.MembershipService;
import cricket.merstham.website.graph.MemberQuery;
import cricket.merstham.website.graph.MembersQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.security.Principal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cricket.merstham.website.frontend.helpers.AttributeConverter.convert;
import static java.lang.Math.min;
import static java.text.MessageFormat.format;

@Controller("AdminMembershipController")
public class MembershipController {

    private final MessageSource messageSource;

    private final MembershipService membershipService;

    @Autowired
    public MembershipController(MessageSource messageSource, MembershipService membershipService) {
        this.messageSource = messageSource;
        this.membershipService = membershipService;
    }

    @GetMapping(value = "/administration/membership", name = "admin-membership-list")
    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public ModelAndView list(Principal principal) {
        return new ModelAndView("administration/membership/list", Map.of(
                "memberColumns", List.of(
                        new DataTableColumn().setKey("membership.family-name").setFieldName("familyName"),
                        new DataTableColumn().setKey("membership.given-name").setFieldName("givenName"),
                        new DataTableColumn().setKey("membership.category").setFieldName("category"),
                        new DataTableColumn().setKey("membership.last-subscription").setFieldName("lastSubscription"))
        ));
    }

    @GetMapping(value = "/administration/membership/edit/{id}", name = "admin-membership-edit")
    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public ModelAndView edit(Principal principal, Locale locale, @PathVariable int id) {
        var member = membershipService.get(id, principal).orElseThrow();
        return new ModelAndView("administration/membership/edit", buildModelData(member, locale));
    }

    private Map<String,?> buildModelData(MemberQuery.Member member, Locale locale) {
        return Map.of(
                "member", member,
                "subscription", member.subscription().get(0),
                "data", member.attributes().stream().collect(Collectors.toMap(
                        a -> a.definition().key(),
                        a -> a.value()
                )),
                "subscriptionHistory", member.subscription().stream().map(
                        s -> Map.of(
                                "membership.year", new DataTableValue().setValue(Integer.toString(s.year())),
                                "membership.description", new DataTableValue().setValue(s.pricelistItem().description()),
                                "membership.category", new DataTableValue().setValue(
                                        messageSource.getMessage(
                                                format("membership.{0}", s.pricelistItem().memberCategory().key()),
                                                null,
                                                locale
                                        )
                                ),
                                "membership.price", new DataTableValue().setValue(NumberFormat.getCurrencyInstance().format(s.price()))
                        )
                ).collect(Collectors.toList()),
                "subscriptionHistoryColumns", List.of(
                        new DataTableColumn().setKey("membership.year"),
                        new DataTableColumn().setKey("membership.description"),
                        new DataTableColumn().setKey("membership.category"),
                        new DataTableColumn().setKey("membership.price")
                ),
                "payments", member.subscription().get(0).order().payment().stream().map(
                        p -> Map.of(
                                "payments.date", new DataTableValue().setValue(
                                        p.date().format(
                                                DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                                        )
                                ),
                                "payments.type", new DataTableValue().setValue(
                                        messageSource.getMessage(
                                                format("payments.{0}-short", p.type()),
                                                null,
                                                locale
                                        )
                                ),
                                "payments.reference", new DataTableValue().setValue(p.reference()),
                                "payments.collected", new DataTableValue().setValue(p.collected() ? "Yes" : "No"),
                                "payments.amount", new DataTableValue().setValue(NumberFormat.getCurrencyInstance().format(p.amount()))
                        )
                ).collect(Collectors.toList()),
                "paymentsColumns", List.of(
                        new DataTableColumn().setKey("payments.date"),
                        new DataTableColumn().setKey("payments.type"),
                        new DataTableColumn().setKey("payments.reference"),
                        new DataTableColumn().setKey("payments.collected"),
                        new DataTableColumn().setKey("payments.amount")
                )
        );
    }

    @PostMapping(value = "/administration/membership/edit/{id}", name = "admin-membership-update")
    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public ModelAndView update(Principal principal, Locale locale, @PathVariable int id, @ModelAttribute("data") GenericForm formData) {
        var attributes = membershipService.getAttributes();
        var member = membershipService.update(
                id,
                principal,
                formData.getData().entrySet().stream().collect(Collectors.toMap(
                a -> a.getKey(),
                a -> convert(attributes, a.getKey(), a.getValue())
        )));
        return new ModelAndView(format("redirect:/administration/membership/edit/{0}", id));
    }

    private String getMemberAttributeString(List<MembersQuery.Attribute> attributeList, String field, String defaultValue) {
        return attributeList.stream()
                .filter(a -> a.definition().key().equals(field))
                .findFirst().map(f -> (String) f.value()).orElse(defaultValue);
    }


    @PostMapping(
            consumes = "application/json",
            produces = "application/json",
            path = "/administration/membership/get-data")
    public @ResponseBody SspResponse<Member> getData(
            Principal principal,
            @RequestBody SspRequest request) {
        List<Member> members = membershipService.getAllMembers(principal).stream().map(
                m -> Member
                        .builder()
                        .id(Integer.toString(m.id()))
                        .familyName(getMemberAttributeString(m.attributes(), "family-name", ""))
                        .givenName(getMemberAttributeString(m.attributes(), "given-name", ""))
                        .category(m.subscription().stream().findFirst().map(s -> s.pricelistItem().description()).orElse("unknown"))
                        .lastSubscription(m.subscription().stream().findFirst().map(s -> Integer.toString(s.year())).orElse("unknown"))
                        .editLink(URI.create(format("/administration/membership/edit/{0}", m.id())))
                        .build()
        ).collect(Collectors.toList());
        return SspResponse.builder(Member.class)
                .withDraw(request.getDraw())
                .withData(members.subList(
                        request.getStart(),
                        min(request.getStart() + request.getLength(), members.size())
                ))
                .withRecordsTotal(members.size())
                .withRecordsFiltered(members.size())
                .build();
    }
}
