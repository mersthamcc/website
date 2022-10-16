package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.model.RegistrationAction;
import cricket.merstham.website.frontend.model.RegistrationBasket;
import cricket.merstham.website.frontend.model.Subscription;
import cricket.merstham.website.frontend.service.MembershipService;
import cricket.merstham.website.frontend.service.payment.PaymentServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
                            subscription));
        } else {
            switch (action) {
                case "add-member":
                    var subscription = new Subscription();
                    subscription
                            .setUuid(UUID.randomUUID())
                            .setAction(RegistrationAction.NEW)
                            .setMember(new HashMap<>());
                    basket.addSubscription(subscription);
                    return new ModelAndView(
                            "registration/select-membership",
                            Map.of(
                                    "categories",
                                    membershipService.getMembershipCategories(),
                                    "subscription",
                                    subscription));
                case "next":
                    return new RedirectView("/register/confirmation");
            }
        }
        return new RedirectView("/register");
    }

    @PostMapping(value = "/register/select-membership", name = "member-details")
    public ModelAndView membershipForm(
            @ModelAttribute("basket") RegistrationBasket basket,
            @ModelAttribute("subscription") Subscription subscription) {
        var membershipCategory =
                membershipService.getMembershipCategory(subscription.getCategory());
        var pricelistItem =
                membershipCategory.getPricelistItem().stream()
                        .filter(p -> p.getId() == subscription.getPricelistItemId())
                        .findFirst()
                        .orElseThrow();

        subscription
                .setPricelistItemId(pricelistItem.getId())
                .setPrice(pricelistItem.getCurrentPrice())
                .updateDefinition(membershipCategory);

        return new ModelAndView(
                "registration/membership-form",
                Map.of(
                        "form", membershipCategory.getForm(),
                        "subscription", basket.updateSubscription(subscription)));
    }

    @PostMapping(value = "/register/add-member", name = "member-details")
    public View membershipFormProcess(
            @ModelAttribute("basket") RegistrationBasket basket,
            @ModelAttribute("subscription") Subscription subscription) {
        basket.updateSubscription(subscription);

        return new RedirectView("/register");
    }

    @GetMapping(value = "/register/confirmation", name = "registration-confirmation")
    public ModelAndView confirmation(
            @ModelAttribute("basket") RegistrationBasket basket,
            @RegisteredOAuth2AuthorizedClient("login") OAuth2AuthorizedClient authorizedClient,
            HttpSession session,
            SessionStatus status) {
        var order =
                membershipService.registerMembersFromBasket(
                        basket, authorizedClient.getAccessToken());
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
