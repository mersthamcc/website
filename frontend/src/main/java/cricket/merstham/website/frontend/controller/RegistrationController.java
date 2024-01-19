package cricket.merstham.website.frontend.controller;

import cricket.merstham.shared.dto.Member;
import cricket.merstham.shared.dto.MemberAttribute;
import cricket.merstham.shared.dto.MemberSubscription;
import cricket.merstham.website.frontend.model.RegistrationBasket;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.MembershipService;
import cricket.merstham.website.frontend.service.payment.PaymentServiceManager;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static cricket.merstham.shared.dto.RegistrationAction.NEW;
import static cricket.merstham.website.frontend.helpers.AttributeConverter.convert;
import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;

@Controller
@SessionAttributes("basket")
@PreAuthorize("isAuthenticated()")
public class RegistrationController {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);

    private MembershipService membershipService;
    private PaymentServiceManager paymentServiceManager;

    @Autowired
    public RegistrationController(
            MembershipService membershipService, PaymentServiceManager paymentServiceManager) {
        this.membershipService = membershipService;
        this.paymentServiceManager = paymentServiceManager;
    }

    @ModelAttribute("basket")
    public RegistrationBasket createRegistrationBasket() {
        LOG.info("Creating new registration basket");
        return new RegistrationBasket();
    }

    @GetMapping(value = "/register", name = "register")
    public ModelAndView register(@ModelAttribute("basket") RegistrationBasket basket) {
        return new ModelAndView("registration/register", Map.of("basket", basket));
    }

    @PostMapping(value = "/register", name = "registration-actions")
    public Object actionProcessor(
            @ModelAttribute("basket") RegistrationBasket basket,
            @ModelAttribute("action") String action,
            @ModelAttribute("delete-member") String deleteMember,
            @ModelAttribute("edit-member") String editMember) {
        if (!deleteMember.isBlank()) {
            basket.removeSuscription(UUID.fromString(deleteMember));
        } else if (!editMember.isBlank()) {
            var subscription = basket.getSubscriptions().get(UUID.fromString(editMember));
            return new ModelAndView(
                    "registration/select-membership",
                    Map.of(
                            "categories",
                            membershipService.getMembershipCategories(),
                            "subscription",
                            subscription,
                            "subscriptionId",
                            editMember));
        } else {
            switch (action) {
                case "add-member":
                    var subscription =
                            MemberSubscription.builder()
                                    .member(Member.builder().build())
                                    .action(NEW)
                                    .build();
                    UUID subscriptionId = UUID.randomUUID();
                    basket.putSubscription(subscriptionId, subscription);
                    return new ModelAndView(
                            "registration/select-membership",
                            Map.of(
                                    "categories",
                                    membershipService.getMembershipCategories(),
                                    "subscription",
                                    subscription,
                                    "subscriptionId",
                                    subscriptionId.toString()));
                case "next":
                    return redirectTo("/register/confirmation");
            }
        }
        return redirectTo("/register");
    }

    private MultiValueMap<String, Object> memberToFormData(MemberSubscription subscription) {
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        subscription.getMember().getAttributeMap().entrySet().stream()
                .forEachOrdered(
                        a -> {
                            if (a.getValue().isArray()) {
                                for (var node : a.getValue()) {
                                    form.add(a.getKey(), node.asText());
                                }
                            } else {
                                form.add(a.getKey(), a.getValue().asText());
                            }
                        });
        return form;
    }

    @PostMapping(value = "/register/select-membership", name = "member-details")
    public ModelAndView membershipForm(
            @ModelAttribute("basket") RegistrationBasket basket,
            @ModelAttribute("category") String category,
            @ModelAttribute("uuid") UUID uuid,
            @ModelAttribute("priceListItemId") Integer priceListItemId) {
        var membershipCategory = membershipService.getMembershipCategory(category);
        var priceListItem =
                membershipCategory.getPriceListItem().stream()
                        .filter(p -> p.getId() == priceListItemId)
                        .findFirst()
                        .orElseThrow();

        var subscription =
                basket.getSubscription(uuid)
                        .setPriceListItem(priceListItem)
                        .setPrice(priceListItem.getCurrentPrice())
                        .setCategory(membershipCategory.getKey());
        basket.putSubscription(uuid, subscription);
        return new ModelAndView(
                "registration/membership-form",
                Map.of(
                        "form", membershipCategory.getForm(),
                        "subscription", subscription,
                        "category", membershipCategory,
                        "subscriptionId", uuid.toString(),
                        "data", memberToFormData(subscription)));
    }

    @PostMapping(
            value = "/register/add-member",
            name = "member-details",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public View membershipFormProcess(
            @ModelAttribute("basket") RegistrationBasket basket,
            @RequestBody MultiValueMap<String, Object> body) {
        var uuid = UUID.fromString((String) body.getFirst("uuid"));
        var subscription = basket.getSubscription(uuid).setMember(memberFromPost(body));
        basket.putSubscription(uuid, subscription);
        return redirectTo("/register");
    }

    private Member memberFromPost(MultiValueMap<String, Object> body) {
        var attributes = membershipService.getAttributes();
        return Member.builder()
                .attributes(
                        attributes.entrySet().stream()
                                .filter(a -> body.containsKey(a.getKey()))
                                .map(
                                        a ->
                                                MemberAttribute.builder()
                                                        .definition(a.getValue())
                                                        .value(
                                                                convert(
                                                                        a.getValue(),
                                                                        body.get(a.getKey())))
                                                        .build())
                                .toList())
                .build();
    }

    @GetMapping(value = "/register/confirmation", name = "registration-confirmation")
    public ModelAndView confirmation(
            @ModelAttribute("basket") RegistrationBasket basket,
            CognitoAuthentication cognitoAuthentication,
            Locale locale,
            HttpSession session,
            SessionStatus status) {
        var order =
                membershipService.registerMembersFromBasket(
                        basket, cognitoAuthentication.getOAuth2AccessToken(), locale);
        status.setComplete();
        session.setAttribute("order", order);
        return new ModelAndView(
                "registration/confirmation",
                Map.of(
                        "order",
                        order,
                        "paymentTypes",
                        paymentServiceManager.getAvailableServices()));
    }
}
