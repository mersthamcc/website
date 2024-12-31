package cricket.merstham.website.frontend.controller;

import cricket.merstham.shared.dto.Member;
import cricket.merstham.shared.dto.MemberAttribute;
import cricket.merstham.shared.dto.MemberCategory;
import cricket.merstham.shared.dto.MemberSubscription;
import cricket.merstham.shared.dto.RegistrationAction;
import cricket.merstham.website.frontend.configuration.RegistrationConfiguration;
import cricket.merstham.website.frontend.model.RegistrationBasket;
import cricket.merstham.website.frontend.model.discounts.Discount;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.MembershipService;
import cricket.merstham.website.frontend.service.payment.PaymentServiceManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.util.Strings;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static cricket.merstham.shared.dto.RegistrationAction.NEW;
import static cricket.merstham.shared.dto.RegistrationAction.NONE;
import static cricket.merstham.shared.dto.RegistrationAction.RENEW;
import static cricket.merstham.website.frontend.helpers.AttributeConverter.convert;
import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;
import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Controller
@SessionAttributes("basket")
@PreAuthorize("isAuthenticated()")
public class RegistrationController {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);
    public static final String CURRENT_SUBSCRIPTION = "current-subscription";
    public static final String ERRORS = "errors";
    public static final String SUBSCRIPTION = "subscription";
    public static final String SUBSCRIPTION_ID = "subscriptionId";
    public static final String MODEL_ERRORS = "errors";
    public static final String REGISTRATION_CLOSED = "registration/closed";

    private final MembershipService membershipService;
    private final PaymentServiceManager paymentServiceManager;
    private final List<Discount> activeDiscounts;
    private final RegistrationConfiguration registrationConfiguration;
    private final boolean enabled;
    private final ModelMapper modelMapper;
    private final int registrationYear;

    @Autowired
    public RegistrationController(
            MembershipService membershipService,
            PaymentServiceManager paymentServiceManager,
            List<Discount> activeDiscounts,
            RegistrationConfiguration registrationConfiguration,
            @Value("${registration.enabled}") boolean enabled,
            ModelMapper modelMapper,
            @Value("${registration.current-year}") int registrationYear) {
        this.membershipService = membershipService;
        this.paymentServiceManager = paymentServiceManager;
        this.activeDiscounts = activeDiscounts;
        this.registrationConfiguration = registrationConfiguration;
        this.enabled = enabled;
        this.modelMapper = modelMapper;
        this.registrationYear = registrationYear;
    }

    @ModelAttribute("basket")
    public RegistrationBasket createRegistrationBasket(
            CognitoAuthentication cognitoAuthentication) {
        LOG.info("Creating new registration basket");
        var basket = new RegistrationBasket(activeDiscounts);
        var members =
                membershipService.getMyMemberDetails(cognitoAuthentication.getOAuth2AccessToken());
        basket.addExistingMembers(members);
        return basket;
    }

    @GetMapping(value = "/register", name = "register")
    public ModelAndView register(
            @ModelAttribute("basket") RegistrationBasket basket, HttpServletRequest request) {
        if (!enabled) {
            return new ModelAndView(REGISTRATION_CLOSED);
        }
        var model = new HashMap<String, Object>();
        var flash = RequestContextUtils.getInputFlashMap(request);
        if (nonNull(flash) && flash.containsKey(ERRORS)) {
            var errors = flash.get(ERRORS);
            model.put(ERRORS, errors);
        }
        model.put("basket", basket);
        model.put("registrationYear", registrationYear);
        return new ModelAndView("registration/register", model);
    }

    @PostMapping(value = "/register", name = "registration-actions")
    public Object actionProcessor(
            @ModelAttribute("basket") RegistrationBasket basket,
            @ModelAttribute("action") String action,
            @ModelAttribute("delete-member") String deleteMember,
            @ModelAttribute("edit-member") String editMember,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        if (!enabled) {
            return redirectTo("/register");
        }
        if (!deleteMember.isBlank()) {
            basket.removeSubscription(UUID.fromString(deleteMember));
        } else if (!editMember.isBlank()) {
            var subscription = basket.getSubscriptions().get(UUID.fromString(editMember));
            setCurrentSubscription(session, subscription);
            return new ModelAndView(
                    "registration/select-membership",
                    Map.of(
                            "categories",
                            membershipService.getMembershipCategories().stream()
                                    .sorted(
                                            Comparator.comparing(MemberCategory::getSortOrder)
                                                    .thenComparing(MemberCategory::getKey))
                                    .toList(),
                            SUBSCRIPTION,
                            subscription,
                            SUBSCRIPTION_ID,
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
                                    membershipService.getMembershipCategories().stream()
                                            .sorted(
                                                    Comparator.comparing(
                                                                    MemberCategory::getSortOrder)
                                                            .thenComparing(MemberCategory::getKey))
                                            .toList(),
                                    SUBSCRIPTION,
                                    subscription,
                                    SUBSCRIPTION_ID,
                                    subscriptionId.toString()));
                case "next":
                    var errors = validateBasket(basket);
                    if (errors.isEmpty()) {
                        return redirectTo("/register/policies");
                    }
                    redirectAttributes.addFlashAttribute(ERRORS, errors);
                    break;
                default:
                    LOG.warn("Unknown action");
            }
        }
        return redirectTo("/register");
    }

    @GetMapping(value = "/register/policies", name = "policies")
    public ModelAndView policies(HttpServletRequest request) {
        if (!enabled) {
            return new ModelAndView(REGISTRATION_CLOSED);
        }
        var model = new HashMap<String, Object>();
        var flash = RequestContextUtils.getInputFlashMap(request);
        if (nonNull(flash) && flash.containsKey(ERRORS)) {
            var errors = flash.get(ERRORS);
            model.put(ERRORS, errors);
        }
        return new ModelAndView("registration/policies", model);
    }

    @PostMapping(value = "/register/policies", name = "accept-policies")
    public RedirectView acceptPolicies(
            @RequestParam(value = "declarations", required = false, defaultValue = "")
                    List<String> declarations,
            RedirectAttributes redirectAttributes) {
        if (!enabled) {
            return redirectTo("/register");
        }
        var errors = validatePolicies(declarations);
        if (errors.isEmpty()) {
            return redirectTo("/register/confirmation");
        }
        redirectAttributes.addFlashAttribute(ERRORS, errors);
        return redirectTo("/register/policies");
    }

    @PostMapping(value = "/register/select-membership", name = "member-details")
    public ModelAndView membershipForm(
            @ModelAttribute("basket") RegistrationBasket basket,
            @ModelAttribute("category") String category,
            @ModelAttribute("uuid") UUID uuid,
            @ModelAttribute("priceListItemId") Integer priceListItemId,
            @ModelAttribute("code") String code,
            HttpSession session,
            CognitoAuthentication authentication) {
        if (!enabled) {
            return new ModelAndView(REGISTRATION_CLOSED);
        }
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

        if (!(Strings.isBlank(membershipCategory.getRegistrationCode()))) {
            if (!(membershipCategory.getRegistrationCode().equals(code)
                    || codePreviouslyEntered(
                            session, category, membershipCategory.getRegistrationCode()))) {
                List<String> errors = List.of();
                if (!Strings.isBlank(code)) {
                    errors = List.of("membership.registration-code-invalid");
                }
                return new ModelAndView(
                        "registration/enter-code",
                        Map.of(
                                MODEL_ERRORS,
                                errors,
                                "category",
                                category,
                                "uuid",
                                uuid.toString(),
                                "priceListItemId",
                                priceListItemId));
            }
            storeCode(session, category, code);
        }

        return new ModelAndView(
                "registration/membership-form",
                Map.of(
                        "form",
                        membershipCategory.getForm(),
                        SUBSCRIPTION,
                        subscription,
                        "category",
                        membershipCategory,
                        SUBSCRIPTION_ID,
                        uuid.toString(),
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
        if (!enabled) {
            return redirectTo("/register");
        }
        var uuid = UUID.fromString((String) body.getFirst("uuid"));
        var subscription = getCurrentSubscription(session);
        updateMember(subscription, memberFromPost(body));
        if (getAction(body).equals(NONE)) {
            subscription.setAction(RENEW);
        }
        session.removeAttribute(CURRENT_SUBSCRIPTION);
        basket.putSubscription(uuid, subscription);
        saveDefaults(body, subscription, session);
        return redirectTo("/register");
    }

    private MemberSubscription updateMember(MemberSubscription subscription, Member member) {
        if (isNull(subscription.getMember())) {
            subscription.setMember(member);
        } else {
            modelMapper.map(member, subscription.getMember());
        }
        return subscription;
    }

    @GetMapping(value = "/register/confirmation", name = "registration-confirmation")
    public ModelAndView confirmation(
            @ModelAttribute("basket") RegistrationBasket basket,
            CognitoAuthentication cognitoAuthentication,
            Locale locale,
            HttpSession session,
            SessionStatus status) {
        if (!enabled) {
            return new ModelAndView(REGISTRATION_CLOSED);
        }
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

    private List<String> validateBasket(RegistrationBasket basket) {
        var errors = new ArrayList<String>();
        if (basket.getBasketTotal().doubleValue() == 0.00) {
            errors.add("membership.errors.no-members");
        }
        return errors;
    }

    private List<String> validatePolicies(List<String> declarations) {
        var errors = new ArrayList<String>();
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
                                    if ((!attributes.containsKey(d)
                                                    || Strings.isBlank(
                                                            (String) attributes.getFirst(d)))
                                            && (sessionDefaults.containsKey(d))) {
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

    private RegistrationAction getAction(MultiValueMap<String, Object> body) {
        return RegistrationAction.valueOf((String) body.getFirst("action"));
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

    private boolean codePreviouslyEntered(
            HttpSession session, String category, String registrationCode) {
        return Objects.equals(session.getAttribute(format("{0}-code", category)), registrationCode);
    }

    private void storeCode(HttpSession session, String category, String registrationCode) {
        session.setAttribute(format("{0}-code", category), registrationCode);
    }
}
