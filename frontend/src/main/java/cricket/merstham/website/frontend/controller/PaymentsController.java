package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.configuration.MailConfiguration;
import cricket.merstham.website.frontend.model.RegistrationBasket;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.EmailService;
import cricket.merstham.website.frontend.service.MembershipService;
import cricket.merstham.website.frontend.service.payment.PaymentServiceManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@SessionAttributes("basket")
public class PaymentsController {

    public static final String ORDER = "current-order";
    private final PaymentServiceManager paymentServiceManager;
    private final MembershipService membershipService;
    private final EmailService emailService;
    private final MailConfiguration mailConfiguration;
    private final MessageSource messageSource;
    private final int registrationYear;

    @Autowired
    public PaymentsController(
            PaymentServiceManager paymentServiceManager,
            MembershipService membershipService,
            EmailService emailService,
            MailConfiguration mailConfiguration,
            MessageSource messageSource,
            @Value("${registration.current-year}") int registrationYear) {
        this.paymentServiceManager = paymentServiceManager;
        this.membershipService = membershipService;
        this.emailService = emailService;
        this.mailConfiguration = mailConfiguration;
        this.messageSource = messageSource;
        this.registrationYear = registrationYear;
    }

    @PostMapping(value = "/payments", name = "payment-start")
    public ModelAndView start(
            @ModelAttribute("basket") RegistrationBasket basket,
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
            @ModelAttribute("basket") RegistrationBasket basket,
            @PathVariable("payment-type") String paymentType,
            CognitoAuthentication cognitoAuthentication,
            HttpServletRequest request) {
        var paymentService = paymentServiceManager.getServiceByName(paymentType);
        return paymentService.authorise(
                request, basket, cognitoAuthentication.getOAuth2AccessToken());
    }

    @GetMapping(value = "/payments/{payment-type}/execute", name = "payment-execute")
    public ModelAndView execute(
            @ModelAttribute("basket") RegistrationBasket basket,
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
            @ModelAttribute("basket") RegistrationBasket basket,
            @PathVariable("payment-type") String paymentType,
            CognitoAuthentication cognitoAuthentication,
            HttpServletRequest request,
            HttpSession session) {
        if (basket.getSubscriptions().isEmpty()) return new ModelAndView("redirect:/registration");
        var paymentService = paymentServiceManager.getServiceByName(paymentType);
        var orderId = (int) session.getAttribute(ORDER);
        var order = membershipService.getOrder(orderId);
        var discountTranslations =
                basket.getDiscounts().keySet().stream()
                        .collect(
                                Collectors.toMap(
                                        Function.identity(),
                                        discount ->
                                                messageSource.getMessage(
                                                        discount,
                                                        null,
                                                        discount,
                                                        request.getLocale())));

        emailService.sendEmail(
                cognitoAuthentication.getOidcUser().getEmail(),
                List.of(),
                mailConfiguration.getConfirmationEmailBcc(),
                "Registration Confirmation",
                paymentService.getConfirmationEmail(),
                paymentService.getEmailModel(
                        Map.of(
                                "basket", basket,
                                "order", order,
                                "paymentType", paymentType,
                                "discountTranslations", discountTranslations,
                                "season", Integer.toString(LocalDate.now().getYear()),
                                "registrationYear", registrationYear)));
        session.removeAttribute(ORDER);
        basket.reset();
        return paymentService.confirm(request, order, cognitoAuthentication.getOAuth2AccessToken());
    }

    @GetMapping(value = "/payments/{payment-type}/cancel", name = "payment-cancel")
    public ModelAndView cancel(
            @ModelAttribute("basket") RegistrationBasket basket,
            @PathVariable("payment-type") String paymentType,
            CognitoAuthentication cognitoAuthentication,
            HttpServletRequest request) {
        var paymentService = paymentServiceManager.getServiceByName(paymentType);
        return paymentService.cancel(request, basket, cognitoAuthentication.getOAuth2AccessToken());
    }
}
