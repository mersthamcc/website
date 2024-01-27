package cricket.merstham.website.frontend.controller;

import cricket.merstham.shared.dto.Member;
import cricket.merstham.shared.dto.MemberAttribute;
import cricket.merstham.shared.dto.MemberSubscription;
import cricket.merstham.website.frontend.configuration.RegistrationConfiguration;
import cricket.merstham.website.frontend.model.RegistrationBasket;
import cricket.merstham.website.frontend.model.discounts.Discount;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.MembershipService;
import cricket.merstham.website.frontend.service.payment.PaymentServiceManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.util.Strings;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static cricket.merstham.shared.dto.RegistrationAction.NEW;
import static cricket.merstham.website.frontend.helpers.AttributeConverter.convert;
import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;
import static java.text.MessageFormat.format;
import static java.util.Objects.nonNull;

@Controller
@SessionAttributes("basket")
@PreAuthorize("isAuthenticated()")
public class RegistrationController {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);
    public static final String CURRENT_SUBSCRIPTION = "current-subscription";
    public static final String ERRORS = "errors";

    private final MembershipService membershipService;
    private final PaymentServiceManager paymentServiceManager;
    private final List<Discount> activeDiscounts;
    private final RegistrationConfiguration registrationConfiguration;

    @Autowired
    public RegistrationController(
            MembershipService membershipService,
            PaymentServiceManager paymentServiceManager,
            List<Discount> activeDiscounts,
            RegistrationConfiguration registrationConfiguration) {
        this.membershipService = membershipService;
        this.paymentServiceManager = paymentServiceManager;
        this.activeDiscounts = activeDiscounts;
        this.registrationConfiguration = registrationConfiguration;
    }

    @ModelAttribute("basket")
    public RegistrationBasket createRegistrationBasket() {
        LOG.info("Creating new registration basket");
        return new RegistrationBasket(activeDiscounts);
    }

    @GetMapping(value = "/register", name = "register")
    public ModelAndView register(
            @ModelAttribute("basket") RegistrationBasket basket, HttpServletRequest request) {
        var model = new HashMap<String, Object>();
        var flash = RequestContextUtils.getInputFlashMap(request);
        if (nonNull(flash) && flash.containsKey(ERRORS)) {
            var errors = flash.get(ERRORS);
            model.put(ERRORS, errors);
        }
        model.put("basket", basket);
        return new ModelAndView("registration/register", model);
    }

    @PostMapping(value = "/register", name = "registration-actions")
    public Object actionProcessor(
            @ModelAttribute("basket") RegistrationBasket basket,
            @ModelAttribute("action") String action,
            @ModelAttribute("delete-member") String deleteMember,
            @ModelAttribute("edit-member") String editMember,
            @RequestParam(value = "declarations", required = false, defaultValue = "")
                    List<String> declarations,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!deleteMember.isBlank()) {
            basket.removeSuscription(UUID.fromString(deleteMember));
        } else if (!editMember.isBlank()) {
            var subscription = basket.getSubscriptions().get(UUID.fromString(editMember));
            setCurrentSubscription(session, subscription);
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
                    setCurrentSubscription(session, subscription);
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
                    var errors = validateBasket(basket, declarations);
                    if (errors.isEmpty()) {
                        return redirectTo("/register/confirmation");
                    }
                    redirectAttributes.addFlashAttribute(ERRORS, errors);
                    break;
            }
        }
        return redirectTo("/register");
    }

    @PostMapping(value = "/register/select-membership", name = "member-details")
    public ModelAndView membershipForm(
            @ModelAttribute("basket") RegistrationBasket basket,
            @ModelAttribute("category") String category,
            @ModelAttribute("uuid") UUID uuid,
            @ModelAttribute("priceListItemId") Integer priceListItemId,
            HttpSession session,
            CognitoAuthentication authentication) {
        var membershipCategory = membershipService.getMembershipCategory(category);
        var priceListItem =
                membershipCategory.getPriceListItem().stream()
                        .filter(p -> p.getId() == priceListItemId)
                        .findFirst()
                        .orElseThrow();

        var subscription =
                getCurrentSubscription(session)
                        .setPriceListItem(priceListItem)
                        .setPrice(priceListItem.getCurrentPrice())
                        .setCategory(membershipCategory.getKey());

        var sessionDefaults = getSessionDefaults(session);

        setCurrentSubscription(session, subscription);
        return new ModelAndView(
                "registration/membership-form",
                Map.of(
                        "form", membershipCategory.getForm(),
                        "subscription", subscription,
                        "category", membershipCategory,
                        "subscriptionId", uuid.toString(),
                        "data",
                                addDefaults(
                                        memberToFormData(subscription),
                                        authentication,
                                        category,
                                        sessionDefaults)));
    }

    @PostMapping(
            value = "/register/add-member",
            name = "member-details",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public View membershipFormProcess(
            @ModelAttribute("basket") RegistrationBasket basket,
            @RequestBody MultiValueMap<String, Object> body,
            HttpSession session) {
        var uuid = UUID.fromString((String) body.getFirst("uuid"));
        var subscription = getCurrentSubscription(session).setMember(memberFromPost(body));
        session.removeAttribute(CURRENT_SUBSCRIPTION);
        basket.putSubscription(uuid, subscription);
        saveDefaults(body, subscription, session);
        return redirectTo("/register");
    }

    @GetMapping(value = "/register/confirmation", name = "registration-confirmation")
    public ModelAndView confirmation(
            @ModelAttribute("basket") RegistrationBasket basket,
            CognitoAuthentication cognitoAuthentication,
            Locale locale,
            HttpSession session,
            SessionStatus status) {
        return new ModelAndView(
                "registration/confirmation",
                Map.of(
                        "basket",
                        basket,
                        "paymentTypes",
                        paymentServiceManager.getEnabledServices()));
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

    private MemberSubscription getCurrentSubscription(HttpSession session) {
        return ((MemberSubscription) session.getAttribute(CURRENT_SUBSCRIPTION));
    }

    private void setCurrentSubscription(HttpSession session, MemberSubscription subscription) {
        session.setAttribute(CURRENT_SUBSCRIPTION, subscription);
    }

    private List<String> validateBasket(RegistrationBasket basket, List<String> declarations) {
        var errors = new ArrayList<String>();
        if (basket.getBasketTotal().doubleValue() == 0.00) {
            errors.add("membership.errors.no-members");
        }
        if (!declarations.contains("terms")) {
            errors.add("membership.errors.accept-terms");
        }
        if (!declarations.contains("policies")) {
            errors.add("membership.errors.accept-policies");
        }
        return errors;
    }

    private MultiValueMap<String, Object> addDefaults(
            MultiValueMap<String, Object> attributes,
            CognitoAuthentication authentication,
            String category,
            Map<String, String> sessionDefaults) {
        var defaults =
                registrationConfiguration.getDefaults().stream()
                        .filter(d -> d.getCategory().equals(category))
                        .findFirst();
        if (defaults.isPresent()) {
            if (nonNull(sessionDefaults)) {
                defaults.get()
                        .getPersistFields()
                        .forEach(
                                d -> {
                                    if ((!attributes.containsKey(d))
                                            || Strings.isBlank((String) attributes.getFirst(d))) {
                                        if (sessionDefaults.containsKey(d))
                                            attributes.put(d, List.of(sessionDefaults.get(d)));
                                    }
                                });
            }
            if (!attributes.containsKey(defaults.get().getEmailField())
                    || Strings.isBlank(
                            (String) attributes.getFirst(defaults.get().getEmailField()))) {
                attributes.put(
                        defaults.get().getEmailField(),
                        List.of(authentication.getOidcUser().getEmail()));
            }
            if (!attributes.containsKey(defaults.get().getNameField())
                    || Strings.isBlank(
                            (String) attributes.getFirst(defaults.get().getNameField()))) {
                var name =
                        format(
                                "{0} {1}",
                                authentication
                                        .getOidcUser()
                                        .getClaims()
                                        .getOrDefault("given_name", ""),
                                authentication
                                        .getOidcUser()
                                        .getClaims()
                                        .getOrDefault("family_name", ""));
                attributes.put(defaults.get().getNameField(), List.of(name));
            }
        }
        return attributes;
    }

    private Map<String, String> getSessionDefaults(HttpSession session) {
        var defaults = session.getAttribute("defaults");
        if (nonNull(defaults) && defaults instanceof Map<?, ?>)
            return (Map<String, String>) defaults;
        return Map.of();
    }

    private void saveDefaults(
            MultiValueMap<String, Object> body,
            MemberSubscription subscription,
            HttpSession session) {
        Map<String, String> sessionDefaults = new HashMap<>();
        var defaults =
                registrationConfiguration.getDefaults().stream()
                        .filter(d -> d.getCategory().equals(subscription.getCategory()))
                        .findFirst();
        if (defaults.isPresent()) {
            defaults.get()
                    .getPersistFields()
                    .forEach(
                            d -> {
                                if (body.containsKey(d)
                                        && !Strings.isBlank((String) body.getFirst(d))) {
                                    sessionDefaults.put(d, (String) body.getFirst(d));
                                }
                            });
            session.setAttribute("defaults", sessionDefaults);
        }
    }
}
