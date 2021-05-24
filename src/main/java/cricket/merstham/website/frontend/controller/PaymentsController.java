package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.model.Order;
import cricket.merstham.website.frontend.service.payment.PaymentService;
import cricket.merstham.website.frontend.service.payment.PaymentServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@SessionAttributes("order")
public class PaymentsController {

    private final PaymentServiceManager paymentServiceManager;

    @Autowired
    public PaymentsController(PaymentServiceManager paymentServiceManager) {
        this.paymentServiceManager = paymentServiceManager;
    }

    @RequestMapping(value = "/payments", name = "payment-start", method = RequestMethod.POST)
    public ModelAndView start(
            @ModelAttribute("order") Order order,
            @ModelAttribute("payment-type") String paymentType) {
        PaymentService paymentService = paymentServiceManager.getServiceByName(paymentType);
        return paymentService.information(order);
    }

    @RequestMapping(
            value = "/payments/{payment-type}/confirmation",
            name = "payment-confirmation",
            method = RequestMethod.GET)
    public ModelAndView confirmation(
            @ModelAttribute("order") Order order,
            @PathVariable("payment-type") String paymentType,
            HttpServletRequest request) {
        PaymentService paymentService = paymentServiceManager.getServiceByName(paymentType);
        return paymentService.confirm(request, order);
    }
}
