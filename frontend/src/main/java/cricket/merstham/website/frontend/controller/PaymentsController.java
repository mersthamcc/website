package cricket.merstham.website.frontend.controller;

import cricket.merstham.shared.dto.Order;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.payment.PaymentServiceManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SessionAttributes("order")
public class PaymentsController {

    private final PaymentServiceManager paymentServiceManager;

    @Autowired
    public PaymentsController(PaymentServiceManager paymentServiceManager) {
        this.paymentServiceManager = paymentServiceManager;
    }

    @PostMapping(value = "/payments", name = "payment-start")
    public ModelAndView start(
            @ModelAttribute("order") Order order,
            @ModelAttribute("payment-type") String paymentType,
            CognitoAuthentication cognitoAuthentication,
            HttpServletRequest request,
            HttpSession session) {
        var paymentService = paymentServiceManager.getServiceByName(paymentType);
        session.setAttribute("payment-type", paymentType);
        return paymentService.checkout(
                request, order, cognitoAuthentication.getOAuth2AccessToken());
    }

    @PostMapping(value = "/payments/{payment-type}/authorise", name = "payment-authorise")
    public ModelAndView authorise(
            @ModelAttribute("order") Order order,
            @PathVariable("payment-type") String paymentType,
            CognitoAuthentication cognitoAuthentication,
            HttpServletRequest request) {
        var paymentService = paymentServiceManager.getServiceByName(paymentType);
        return paymentService.authorise(
                request, order, cognitoAuthentication.getOAuth2AccessToken());
    }

    @GetMapping(value = "/payments/{payment-type}/execute", name = "payment-execute")
    public ModelAndView execute(
            @ModelAttribute("order") Order order,
            @PathVariable("payment-type") String paymentType,
            CognitoAuthentication cognitoAuthentication,
            HttpServletRequest request) {
        var paymentService = paymentServiceManager.getServiceByName(paymentType);
        return paymentService.execute(request, order, cognitoAuthentication.getOAuth2AccessToken());
    }

    @GetMapping(value = "/payments/{payment-type}/confirmation", name = "payment-confirmation")
    public ModelAndView confirmation(
            @ModelAttribute("order") Order order,
            @PathVariable("payment-type") String paymentType,
            CognitoAuthentication cognitoAuthentication,
            HttpServletRequest request,
            SessionStatus status) {
        var paymentService = paymentServiceManager.getServiceByName(paymentType);
        status.setComplete();
        return paymentService.confirm(request, order, cognitoAuthentication.getOAuth2AccessToken());
    }

    @GetMapping(value = "/payments/{payment-type}/cancel", name = "payment-cancel")
    public ModelAndView cancel(
            @ModelAttribute("order") Order order,
            @PathVariable("payment-type") String paymentType,
            CognitoAuthentication cognitoAuthentication,
            HttpServletRequest request) {
        var paymentService = paymentServiceManager.getServiceByName(paymentType);
        return paymentService.cancel(request, order, cognitoAuthentication.getOAuth2AccessToken());
    }
}
