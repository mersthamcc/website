package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.website.frontend.model.GenericForm;
import cricket.merstham.website.frontend.service.MembershipService;
import cricket.merstham.website.graph.MembersQuery;
import cricket.merstham.website.graph.UpdateMemberMutation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cricket.merstham.website.frontend.helpers.AttributeConverter.convert;

@Controller("AdminMembershipController")
public class MembershipController {

    private final MembershipService membershipService;

    @Autowired
    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping(value = "/administration/membership", name = "admin-membership-list")
    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public ModelAndView list(Principal principal) {
        var members = membershipService.getAllMembers(principal).stream().map(
                m -> Map.of(
                        "id", m.id(),
                        "givenName", getMemberAttributeString(m.attributes(), "given-name", ""),
                        "familyName", getMemberAttributeString(m.attributes(), "family-name", ""),
                        "category", m.subscription().stream().findFirst().map(s -> s.pricelistItem().description()).orElse("unknown"),
                        "lastSubscription", m.subscription().stream().findFirst().map(s -> Integer.toString(s.year())).orElse("unknown")
                )
        ).collect(Collectors.toList());
        return new ModelAndView("administration/membership/list", Map.of("members", members));
    }

    @GetMapping(value = "/administration/membership/edit/{id}", name = "admin-membership-edit")
    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public ModelAndView edit(Principal principal, @PathVariable int id) {
        var member = membershipService.get(id, principal).orElseThrow();
        return new ModelAndView("administration/membership/edit",
                Map.of(
                        "member", member,
                        "form", member.subscription().get(0).pricelistItem().memberCategory().form(),
                        "data", member.attributes().stream().collect(Collectors.toMap(
                                a -> a.definition().key(),
                                a -> a.value()
                        ))
                )
        );
    }

    @PostMapping(value = "/administration/membership/edit/{id}", name = "admin-membership-update")
    @PreAuthorize("hasRole('ROLE_MEMBERSHIP')")
    public ModelAndView update(Principal principal, @PathVariable int id, @ModelAttribute("data") GenericForm formData) {
        var attributes = membershipService.getAttributes();
        var member = membershipService.update(
                id,
                principal,
                formData.getData().entrySet().stream().collect(Collectors.toMap(
                a -> a.getKey(),
                a -> convert(attributes, a.getKey(), a.getValue())
        )));
        return new ModelAndView("administration/membership/edit",
                Map.of(
                        "form", member.subscription().get(0).pricelistItem().memberCategory().form(),
                        "data", member.attributes().stream().collect(Collectors.toMap(
                                a -> a.definition().key(),
                                a -> a.value()
                        ))
                )
        );
    }

    private String getMemberAttributeString(List<MembersQuery.Attribute> attributeList, String field, String defaultValue) {
        return attributeList.stream()
                .filter(a -> a.definition().key().equals(field))
                .findFirst().map(f -> (String) f.value()).orElse(defaultValue);
    }
}
