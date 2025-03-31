package cricket.merstham.website.frontend.controller;

import cricket.merstham.shared.dto.Order;
import cricket.merstham.website.frontend.model.RegistrationBasket;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.MembershipService;
import cricket.merstham.website.frontend.service.payment.PaymentServiceManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import static cricket.merstham.website.frontend.controller.RegistrationController.BASKET;
import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectToPage;
import static java.util.Objects.isNull;

@Controller
@SessionAttributes(BASKET)
@PreAuthorize("isAuthenticated()")
public class PaymentsController {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentsController.class);

    public static final String ORDER = "current-order";
    private final PaymentServiceManager paymentServiceManager;
    private final MembershipService membershipService;

    @Autowired
    public PaymentsController(
            PaymentServiceManager paymentServiceManager, MembershipService membershipService) {
        this.paymentServiceManager = paymentServiceManager;
        this.membershipService = membershipService;
    }

    @PostMapping(value = "/payments", name = "payment-start")
    public ModelAndView start(
            @ModelAttribute(BASKET) RegistrationBasket basket,
            @ModelAttribute("payment-type") String paymentType,
            CognitoAuthentication cognitoAuthentication,
            HttpServletRequest request,
            HttpSession session) {
        var paymentService = paymentServiceManager.getServiceByName(paymentType);
        session.setAttribute("payment-type", paymentType);
        return paymentService.checkout(
                request, basket, cognitoAuthentication.getOAuth2AccessToken());
    }

    @PostMapping(value = "/payments/{payment-type}/authorise", name = "payment-authorise")
    public ModelAndView authorise(
            @ModelAttribute(BASKET) RegistrationBasket basket,
            @PathVariable("payment-type") String paymentType,
            CognitoAuthentication cognitoAuthentication,
            HttpServletRequest request) {
        var paymentService = paymentServiceManager.getServiceByName(paymentType);
        return paymentService.authorise(
                request, basket, cognitoAuthentication.getOAuth2AccessToken());
    }

    @GetMapping(value = "/payments/{payment-type}/execute", name = "payment-execute")
    public ModelAndView execute(
            @ModelAttribute(BASKET) RegistrationBasket basket,
            @PathVariable("payment-type") String paymentType,
            CognitoAuthentication cognitoAuthentication,
            HttpServletRequest request,
            HttpSession session) {
        var paymentService = paymentServiceManager.getServiceByName(paymentType);
        var order =
                membershipService.registerMembersFromBasket(
                        basket, cognitoAuthentication.getOAuth2AccessToken(), request.getLocale());
        session.setAttribute(ORDER, order.getId());
        return paymentService.execute(
                request, basket, order, cognitoAuthentication.getOAuth2AccessToken());
    }

    @GetMapping(value = "/payments/{payment-type}/confirmation", name = "payment-confirmation")
    public ModelAndView confirmation(
            @ModelAttribute(BASKET) RegistrationBasket basket,
            @PathVariable("payment-type") String paymentType,
            CognitoAuthentication cognitoAuthentication,
            HttpServletRequest request,
            HttpSession session,
            SessionStatus status) {
        if (basket.getChargeableSubscriptions().isEmpty())
            return new ModelAndView("redirect:/registration");
        var paymentService = paymentServiceManager.getServiceByName(paymentType);
        var orderId = (Integer) session.getAttribute(ORDER);
        Order order;
        if (isNull(orderId)) {
            order =
                    membershipService.registerMembersFromBasket(
                            basket,
                            cognitoAuthentication.getOAuth2AccessToken(),
                            request.getLocale());
        } else {
            order = membershipService.getOrder(orderId);
        }
        membershipService.confirmOrder(
                order, paymentType, cognitoAuthentication.getOAuth2AccessToken());

        session.removeAttribute(ORDER);
        session.removeAttribute(BASKET);
        status.setComplete();
        return paymentService.confirm(request, order, cognitoAuthentication.getOAuth2AccessToken());
    }

    @GetMapping(value = "/payments/{payment-type}/cancel", name = "payment-cancel")
    public ModelAndView cancel(
            @ModelAttribute(BASKET) RegistrationBasket basket,
            @PathVariable("payment-type") String paymentType,
            CognitoAuthentication cognitoAuthentication,
            HttpServletRequest request) {
        var paymentService = paymentServiceManager.getServiceByName(paymentType);
        return paymentService.cancel(request, basket, cognitoAuthentication.getOAuth2AccessToken());
    }

    @ExceptionHandler(value = IllegalStateException.class)
    public ModelAndView handleException(IllegalStateException ex) {
        LOG.warn("Encountered invalid session error, redirecting to /register", ex);
        return redirectToPage("/register");
    }
}
