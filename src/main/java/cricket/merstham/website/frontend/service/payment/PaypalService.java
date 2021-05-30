package cricket.merstham.website.frontend.service.payment;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import cricket.merstham.website.frontend.configuration.ClubConfiguration;
import cricket.merstham.website.frontend.model.Order;
import cricket.merstham.website.frontend.service.MembershipService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.text.MessageFormat.format;

@Service("paypal")
public class PaypalService implements PaymentService {

    private static final String SERVICE_NAME = "paypal";
    private static final String CAPTURE_INTENT = "CAPTURE";
    private static final String APPROVAL_LINK = "approve";
    private static final String REDIRECT_FORMAT = "redirect:{0}";
    private static final String PAYPAL_ORDER_SESSION_ATTRIBUTE = "paypal-order";

    private final boolean enabled;
    private final String disabledReason;
    private final ClubConfiguration clubConfiguration;
    private final MembershipService membershipService;

    // Creating a client for the environment
    private final PayPalHttpClient client;

    public PaypalService(
            @Value("${payments.paypal.enabled}") boolean enabled,
            @Value("${payments.paypal.disabled-reason}") String disabledReason,
            @Value("${payments.paypal.client-id}") String clientId,
            @Value("${payments.paypal.client-secret}") String clientSecret,
            @Value("${payments.paypal.sandbox}") boolean sandbox,
            ClubConfiguration clubConfiguration,
            MembershipService membershipService) {
        this.enabled = enabled;
        this.disabledReason = disabledReason;
        this.clubConfiguration = clubConfiguration;
        this.membershipService = membershipService;
        PayPalEnvironment environment;
        if (sandbox) {
            environment = new PayPalEnvironment.Sandbox(clientId, clientSecret);
        } else {
            environment = new PayPalEnvironment.Live(clientId, clientSecret);
        }
        this.client = new PayPalHttpClient(environment);
    }

    @Override
    public String getName() {
        return SERVICE_NAME;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Optional<String> getDisabledReason() {
        return Optional.ofNullable(disabledReason);
    }

    @Override
    public ModelAndView checkout(HttpServletRequest request, Order order) {
        return new ModelAndView("payments/paypal/checkout");
    }

    @Override
    public ModelAndView authorise(HttpServletRequest request, Order order) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent(CAPTURE_INTENT);

        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        purchaseUnits.add(
                new PurchaseUnitRequest()
                        .description(order.getWebReference())
                        .amountWithBreakdown(
                                new AmountWithBreakdown()
                                        .currencyCode("GBP")
                                        .value(order.getTotal().toPlainString())));
        orderRequest.purchaseUnits(purchaseUnits);
        URI requestUri = URI.create(request.getRequestURL().toString());
        String baseUri = format("{0}://{1}", requestUri.getScheme(), requestUri.getAuthority());
        orderRequest.applicationContext(
                new ApplicationContext()
                        .brandName(clubConfiguration.getClubName())
                        .returnUrl(format("{0}/payments/{1}/execute", baseUri, SERVICE_NAME))
                        .cancelUrl(format("{0}/payments/{1}/cancel", baseUri, SERVICE_NAME)));
        try {
            var apiRequest = new OrdersCreateRequest().requestBody(orderRequest);
            HttpResponse<com.paypal.orders.Order> result = client.execute(apiRequest);

            com.paypal.orders.Order paypalOrder = result.result();
            String authoriseUrl =
                    paypalOrder.links().stream()
                            .filter(l -> l.rel().equals(APPROVAL_LINK))
                            .findFirst()
                            .orElseThrow()
                            .href();

            request.getSession().setAttribute(PAYPAL_ORDER_SESSION_ATTRIBUTE, paypalOrder.id());
            return new ModelAndView(format(REDIRECT_FORMAT, authoriseUrl));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ModelAndView execute(HttpServletRequest request, Order order) {
        OrdersCaptureRequest captureRequest =
                new OrdersCaptureRequest(
                        (String) request.getSession().getAttribute(PAYPAL_ORDER_SESSION_ATTRIBUTE));

        try {
            HttpResponse<com.paypal.orders.Order> response = client.execute(captureRequest);
            com.paypal.orders.Order paypalOrder = response.result();

            List<Capture> captures = paypalOrder.purchaseUnits().get(0).payments().captures();

            captures.forEach(
                    capture ->
                            membershipService.createPayment(
                                    order,
                                    SERVICE_NAME,
                                    capture.id(),
                                    ZonedDateTime.parse(capture.createTime()).toLocalDateTime(),
                                    new BigDecimal(capture.amount().value()),
                                    new BigDecimal(
                                            capture.sellerReceivableBreakdown()
                                                    .paypalFee()
                                                    .value()),
                                    false,
                                    false,
                                    request.getUserPrincipal()));

            return new ModelAndView(format("redirect:/payments/{0}/confirmation", SERVICE_NAME));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ModelAndView confirm(HttpServletRequest request, Order order) {
        OrdersGetRequest ordersGetRequest =
                new OrdersGetRequest(
                        (String) request.getSession().getAttribute(PAYPAL_ORDER_SESSION_ATTRIBUTE));
        request.getSession().removeAttribute(PAYPAL_ORDER_SESSION_ATTRIBUTE);
        try {
            HttpResponse<com.paypal.orders.Order> response = client.execute(ordersGetRequest);
            com.paypal.orders.Order paypalOrder = response.result();

            return new ModelAndView(
                    "payments/paypal/confirmation",
                    Map.of("order", order, "paypal-order", paypalOrder));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ModelAndView cancel(HttpServletRequest request, Order order) {
        return null;
    }
}
