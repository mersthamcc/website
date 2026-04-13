package cricket.merstham.website.frontend.controller;

import com.gocardless.resources.Mandate;
import cricket.merstham.shared.dto.Order;
import cricket.merstham.shared.dto.Pass;
import cricket.merstham.shared.dto.User;
import cricket.merstham.website.frontend.model.ChangePassword;
import cricket.merstham.website.frontend.model.payment.PaymentSchedule;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.CognitoService;
import cricket.merstham.website.frontend.service.MembershipService;
import cricket.merstham.website.frontend.service.payment.GoCardlessService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;
import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectToPage;
import static cricket.merstham.website.frontend.service.payment.GoCardlessService.SESSION_DAY_OF_MONTH;
import static cricket.merstham.website.frontend.service.payment.GoCardlessService.SESSION_FLOW_ID;
import static cricket.merstham.website.frontend.service.payment.GoCardlessService.SESSION_NUMBER_OF_PAYMENTS;
import static cricket.merstham.website.frontend.service.payment.GoCardlessService.SESSION_SCHEDULES;
import static java.text.MessageFormat.format;
import static java.util.Objects.nonNull;

@Controller
@PreAuthorize("isAuthenticated()")
public class AccountController {

    public static final String ERRORS = "ERRORS";
    private static final String INFO = "INFO";
    public static final String ERROR_KEY = "errors";

    private final CognitoService service;
    private final MembershipService membershipService;
    private final GoCardlessService goCardlessService;

    @Autowired
    public AccountController(
            CognitoService service,
            MembershipService membershipService,
            GoCardlessService goCardlessService) {
        this.service = service;
        this.membershipService = membershipService;
        this.goCardlessService = goCardlessService;
    }

    @GetMapping(value = "/account", name = "account-members")
    public ModelAndView home(
            HttpServletRequest request, CognitoAuthentication cognitoAuthentication) {
        var model = baseModel(request);
        model.put(
                "members",
                membershipService.getMyMembers(cognitoAuthentication.getOAuth2AccessToken()));
        return new ModelAndView(
                "account/member-list",
                model,
                model.containsKey(ERROR_KEY) ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK);
    }

    @GetMapping(value = "/account/billing", name = "account-members-billing")
    public ModelAndView billingHome(
            HttpServletRequest request, CognitoAuthentication cognitoAuthentication) {
        var model = baseModel(request);
        model.put(
                "orders",
                membershipService.getOrders(cognitoAuthentication.getOAuth2AccessToken()).stream()
                        .sorted(Comparator.comparing(Order::getCreateDate).reversed())
                        .toList());
        return new ModelAndView(
                "account/billing",
                model,
                model.containsKey(ERROR_KEY) ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK);
    }

    @GetMapping(value = "/account/billing/{uuid}", name = "account-members-billing-order")
    public ModelAndView billingHome(
            @PathVariable String uuid,
            HttpServletRequest request,
            CognitoAuthentication cognitoAuthentication) {
        var model = baseModel(request);
        var order =
                membershipService.getOrders(cognitoAuthentication.getOAuth2AccessToken()).stream()
                        .filter(o -> o.getUuid().equals(uuid))
                        .findFirst()
                        .orElseThrow();

        model.put("order", order);
        return new ModelAndView(
                "account/order",
                model,
                model.containsKey(ERROR_KEY) ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK);
    }

    @GetMapping(value = "/account/billing/{uuid}/new-mandate", name = "account-members-new-mandate")
    public ModelAndView newMandate(
            @PathVariable String uuid,
            HttpServletRequest request,
            CognitoAuthentication cognitoAuthentication) {
        var order =
                membershipService.getOrders(cognitoAuthentication.getOAuth2AccessToken()).stream()
                        .filter(o -> o.getUuid().equals(uuid))
                        .findFirst()
                        .orElseThrow();

        var model =
                goCardlessService.newDirectDebitViewModel(
                        request,
                        order.getOutstanding(),
                        cognitoAuthentication.getOAuth2AccessToken());
        model.putAll(baseModel(request));
        model.put("order", order);
        model.put("existingMandates", List.of());

        return new ModelAndView("account/new-mandate", model);
    }

    @PostMapping(
            value = "/account/billing/{uuid}/new-mandate/authorise",
            name = "account-members-new-mandate")
    public ModelAndView newMandateProcess(
            @PathVariable String uuid,
            HttpServletRequest request,
            CognitoAuthentication cognitoAuthentication) {
        request.getSession()
                .setAttribute(
                        SESSION_NUMBER_OF_PAYMENTS,
                        Integer.parseInt(request.getParameter("number_payments")));
        request.getSession()
                .setAttribute(
                        SESSION_DAY_OF_MONTH,
                        Integer.parseInt(request.getParameter("payment_day")));
        return goCardlessService.startRedirectFlow(
                request, format("/account/billing/{0}/new-mandate/confirm", uuid), uuid);
    }

    @GetMapping(value = "/account/billing/{uuid}/new-mandate/confirm")
    public ModelAndView newMandateConfirm(
            @PathVariable String uuid,
            HttpServletRequest request,
            CognitoAuthentication cognitoAuthentication) {

        var order =
                membershipService.getOrders(cognitoAuthentication.getOAuth2AccessToken()).stream()
                        .filter(o -> o.getUuid().equals(uuid))
                        .findFirst()
                        .orElseThrow();

        int dayOfMonth = (int) request.getSession().getAttribute(SESSION_DAY_OF_MONTH);
        int numberOfPayments = (int) request.getSession().getAttribute(SESSION_NUMBER_OF_PAYMENTS);
        String flowId = (String) request.getSession().getAttribute(SESSION_FLOW_ID);
        List<PaymentSchedule> schedules =
                (List<PaymentSchedule>) request.getSession().getAttribute(SESSION_SCHEDULES);

        var paymentSchedule =
                schedules.stream()
                        .filter(ps -> ps.getNumberOfPayments() == numberOfPayments)
                        .findFirst()
                        .orElseThrow();

        Mandate mandate =
                goCardlessService.getMandateFromFlowId(
                        flowId, uuid, cognitoAuthentication.getOAuth2AccessToken());

        goCardlessService.createPayments(
                mandate,
                cognitoAuthentication.getOAuth2AccessToken(),
                dayOfMonth,
                numberOfPayments,
                paymentSchedule,
                order,
                order.getOutstanding());
        goCardlessService.clearSession(request);
        return redirectToPage("/account/billing/" + uuid);
    }

    @GetMapping(value = "/account/info", name = "account-user")
    public ModelAndView userinfo(HttpServletRequest request) {
        var model = baseModel(request);
        return new ModelAndView(
                "account/userinfo",
                model,
                model.containsKey(ERROR_KEY) ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK);
    }

    @PostMapping(value = "/account/info", name = "account-user-update-details")
    public RedirectView updateUser(User user, RedirectAttributes redirectAttributes) {
        var errors = service.updateUser(user);
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute(ERRORS, errors);
        } else {
            redirectAttributes.addFlashAttribute(INFO, List.of("account.success.update-details"));
        }
        return redirectTo("/account/info");
    }

    @GetMapping(value = "/account/security", name = "account-user-security")
    public ModelAndView securityHome(HttpServletRequest request) {
        var model = baseModel(request);

        if (service.isIdentityProviderUser()) {
            return new ModelAndView("account/idp-password", model);
        } else {
            var requirements = service.getPasswordRequirements();
            model.put("passwordRequirements", requirements);

            return new ModelAndView(
                    "account/change-password",
                    model,
                    model.containsKey(ERROR_KEY)
                            ? HttpStatus.INTERNAL_SERVER_ERROR
                            : HttpStatus.OK);
        }
    }

    @PostMapping(value = "/account/change-password", name = "account-user-change-password")
    public RedirectView changePassword(
            @Valid ChangePassword changePassword,
            Errors errors,
            RedirectAttributes redirectAttributes) {

        if (errors.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    ERRORS,
                    errors.getAllErrors().stream()
                            .map(e -> format("account.error.{0}", e.getDefaultMessage()))
                            .toList());
        } else {
            var result = service.changePassword(changePassword);
            if (!result.isEmpty()) {
                redirectAttributes.addFlashAttribute(ERRORS, result);
            } else {
                redirectAttributes.addFlashAttribute(
                        INFO, List.of("account.success.password-change"));
            }
        }
        return redirectTo("/account/security");
    }

    @GetMapping(value = "/account/pass/{uuid}/apple", name = "apple-wallet-membership-card")
    public ResponseEntity<Resource> appleWalletMemberShipCard(
            @PathVariable String uuid, CognitoAuthentication cognitoAuthentication) {
        Pass pass = membershipService.getMemberPass(uuid, "apple", cognitoAuthentication);
        var decoder = Base64.getDecoder();
        var entity = new ByteArrayResource(decoder.decode(pass.getContent()));
        return ResponseEntity.ok()
                .contentLength(entity.contentLength())
                .contentType(MediaType.valueOf("application/vnd.apple.pkpass"))
                .body(entity);
    }

    @GetMapping(value = "/account/pass/{uuid}/google", name = "google-wallet-membership-card")
    public RedirectView googleWalletMemberShipCard(
            @PathVariable String uuid, CognitoAuthentication cognitoAuthentication) {
        Pass pass = membershipService.getMemberPass(uuid, "google", cognitoAuthentication);
        return redirectTo(pass.getContent());
    }

    private Map<String, Object> baseModel(HttpServletRequest request) {
        var model = new HashMap<String, Object>();
        model.put("userDetails", service.getUserDetails());

        var flash = RequestContextUtils.getInputFlashMap(request);
        if (nonNull(flash)) {
            if (flash.containsKey(ERRORS)) model.put(ERROR_KEY, flash.get(ERRORS));
            if (flash.containsKey(INFO)) model.put("info", flash.get(INFO));
        }
        return model;
    }
}
