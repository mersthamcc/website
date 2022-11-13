package cricket.merstham.website.frontend.service.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import cricket.merstham.shared.dto.Order;
import cricket.merstham.website.frontend.service.MembershipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import static java.text.MessageFormat.format;

@Service("stripe")
public class StripeService implements PaymentService {
    private static final Logger LOG = LoggerFactory.getLogger(StripeService.class);

    private static final String SERVICE_NAME = "stripe";
    private static final String STRIPE_SESSION_ATTRIBUTE = "stripe-session-id";

    private final boolean enabled;
    private final String disabledReason;
    private final MembershipService membershipService;
    private final String apiKey;
    private final String publishableKey;

    public StripeService(
            @Value("${payments.stripe.enabled}") boolean enabled,
            @Value("${payments.stripe.disabled-reason}") String disabledReason,
            @Value("${payments.stripe.api-key}") String apiKey,
            @Value("${payments.stripe.publishable-key}") String publishableKey,
            MembershipService membershipService) {
        this.enabled = enabled;
        this.disabledReason = disabledReason;
        this.membershipService = membershipService;
        this.apiKey = apiKey;
        this.publishableKey = publishableKey;
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
    public ModelAndView checkout(
            HttpServletRequest request, Order order, OAuth2AccessToken accessToken) {
        var requestUri = URI.create(request.getRequestURL().toString());
        String baseUri = format("{0}://{1}", requestUri.getScheme(), requestUri.getAuthority());

        SessionCreateParams.Builder params =
                SessionCreateParams.builder()
                        .setCustomerEmail(request.getUserPrincipal().getName())
                        .setClientReferenceId(order.getWebReference())
                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(format("{0}/payments/{1}/execute", baseUri, SERVICE_NAME))
                        .setCancelUrl(format("{0}/payments/{1}/cancel", baseUri, SERVICE_NAME))
                        .setLocale(SessionCreateParams.Locale.EN_GB);

        order.getMemberSubscription()
                .forEach(
                        subscription ->
                                params.addLineItem(
                                        SessionCreateParams.LineItem.builder()
                                                .setQuantity(1L)
                                                .setPriceData(
                                                        SessionCreateParams.LineItem.PriceData
                                                                .builder()
                                                                .setCurrency("gbp")
                                                                .setUnitAmount(
                                                                        toStripeLongValue(
                                                                                subscription
                                                                                        .getPrice()))
                                                                .setProductData(
                                                                        SessionCreateParams.LineItem
                                                                                .PriceData
                                                                                .ProductData
                                                                                .builder()
                                                                                .setName(
                                                                                        format(
                                                                                                "{0} {1}",
                                                                                                subscription
                                                                                                        .getMember()
                                                                                                        .getAttributeMap()
                                                                                                        .get(
                                                                                                                "given-name"),
                                                                                                subscription
                                                                                                        .getMember()
                                                                                                        .getAttributeMap()
                                                                                                        .get(
                                                                                                                "family-name")))
                                                                                .build())
                                                                .build())
                                                .build()));

        try {
            var session =
                    Session.create(
                            params.build(), RequestOptions.builder().setApiKey(apiKey).build());
            LOG.info("Successfully create Stripe session: {}", session.getId());
            request.getSession().setAttribute(STRIPE_SESSION_ATTRIBUTE, session.getId());
            return new ModelAndView(
                    "payments/stripe/checkout",
                    Map.of("sessionId", session.getId(), "publishableKey", publishableKey));
        } catch (StripeException e) {
            LOG.error("Error creating Stripe session", e);
            throw new RuntimeException("Error creating Stripe session", e);
        }
    }

    @Override
    public ModelAndView authorise(
            HttpServletRequest request, Order order, OAuth2AccessToken accessToken) {
        return null;
    }

    @Override
    public ModelAndView execute(
            HttpServletRequest request, Order order, OAuth2AccessToken accessToken) {
        try {
            var session =
                    Session.retrieve(
                            (String) request.getSession().getAttribute(STRIPE_SESSION_ATTRIBUTE),
                            RequestOptions.builder().setApiKey(apiKey).build());
            var paymentIntent =
                    PaymentIntent.retrieve(
                            session.getPaymentIntent(),
                            RequestOptions.builder().setApiKey(apiKey).build());
            membershipService.createPayment(
                    order,
                    SERVICE_NAME,
                    paymentIntent.getId(),
                    ZonedDateTime.ofInstant(
                                    Instant.ofEpochSecond(paymentIntent.getCreated()),
                                    ZoneId.systemDefault())
                            .toLocalDateTime(),
                    fromStripeLongValue(paymentIntent.getAmount()),
                    BigDecimal.ZERO,
                    false,
                    false,
                    accessToken);
            return new ModelAndView(format("redirect:/payments/{0}/confirmation", SERVICE_NAME));

        } catch (StripeException e) {
            LOG.error("Error getting payment details from Stripe", e);
            throw new RuntimeException("Error getting payment details from Stripe", e);
        }
    }

    @Override
    public ModelAndView confirm(
            HttpServletRequest request, Order order, OAuth2AccessToken accessToken) {
        return new ModelAndView("payments/stripe/confirmation");
    }

    @Override
    public ModelAndView cancel(
            HttpServletRequest request, Order order, OAuth2AccessToken accessToken) {
        return null;
    }

    private long toStripeLongValue(BigDecimal value) {
        return value.multiply(BigDecimal.valueOf(100L)).longValue();
    }

    private BigDecimal fromStripeLongValue(long value) {
        return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100L));
    }
}
