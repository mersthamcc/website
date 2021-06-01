package cricket.merstham.website.frontend.service.payment;

import com.gocardless.GoCardlessClient;
import com.gocardless.resources.Customer;
import com.gocardless.resources.Mandate;
import com.gocardless.resources.Payment;
import com.gocardless.resources.RedirectFlow;
import com.gocardless.services.RedirectFlowService;
import cricket.merstham.website.frontend.model.Order;
import cricket.merstham.website.frontend.model.payment.PaymentSchedule;
import cricket.merstham.website.frontend.service.MembershipService;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.gocardless.GoCardlessClient.Environment.LIVE;
import static com.gocardless.GoCardlessClient.Environment.SANDBOX;
import static com.gocardless.services.PaymentService.PaymentCreateRequest.Currency.GBP;
import static java.text.MessageFormat.format;

@Service("gocardless")
public class GoCardlessService implements PaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(GoCardlessService.class);

    private static final String SERVICE_NAME = "gocardless";
    private static final String REDIRECT_FORMAT = "redirect:{0}";
    private static final String SESSION_NUMBER_OF_PAYMENTS = "gc-number-of-payments";
    private static final String SESSION_DAY_OF_MONTH = "gc-day-of-month";
    private static final String SESSION_SCHEDULES = "gc-schedules";
    private static final String SESSION_FLOW_ID = "gc-flow-id";

    private final boolean enabled;
    private final String disabledReason;
    private final MembershipService membershipService;
    private final String mandateDescription;

    private final GoCardlessClient client;

    public GoCardlessService(
            @Value("${payments.gocardless.enabled}") boolean enabled,
            @Value("${payments.gocardless.disabled-reason}") String disabledReason,
            @Value("${payments.gocardless.access-token}") String accessToken,
            @Value("${payments.gocardless.sandbox}") boolean sandbox,
            @Value("${payments.gocardless.mandate-description}") String mandateDescription,
            MembershipService membershipService) {
        this.enabled = enabled;
        this.disabledReason = disabledReason;
        this.membershipService = membershipService;
        this.mandateDescription = mandateDescription;
        this.client =
                GoCardlessClient.newBuilder(accessToken)
                        .withEnvironment(sandbox ? SANDBOX : LIVE)
                        .build();
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
        List<PaymentSchedule> schedules = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            BigDecimal monthly = order.getTotal().divide(BigDecimal.valueOf(i), 2, RoundingMode.UP);
            schedules.add(
                    new PaymentSchedule()
                            .setNumberOfPayments(i)
                            .setAmount(monthly)
                            .setFinalAmount(
                                    order.getTotal()
                                            .subtract(
                                                    monthly.multiply(BigDecimal.valueOf(i - 1)))));
        }

        request.getSession().setAttribute(SESSION_SCHEDULES, schedules);
        return new ModelAndView("payments/gocardless/checkout", Map.of("schedules", schedules));
    }

    @Override
    public ModelAndView authorise(HttpServletRequest request, Order order) {
        URI requestUri = URI.create(request.getRequestURL().toString());
        String baseUri = format("{0}://{1}", requestUri.getScheme(), requestUri.getAuthority());
        KeycloakAuthenticationToken keycloakAuthenticationToken =
                (KeycloakAuthenticationToken) request.getUserPrincipal();
        KeycloakPrincipal keycloakPrincipal =
                (KeycloakPrincipal) keycloakAuthenticationToken.getPrincipal();
        RedirectFlow redirectFlow =
                client.redirectFlows()
                        .create()
                        .withDescription(mandateDescription)
                        .withIdempotencyKey(UUID.randomUUID().toString())
                        .withSessionToken(request.getRequestedSessionId())
                        .withSuccessRedirectUrl(
                                format("{0}/payments/{1}/execute", baseUri, SERVICE_NAME))
                        .withPrefilledCustomerEmail(request.getUserPrincipal().getName())
                        .withPrefilledCustomerGivenName(
                                keycloakPrincipal
                                        .getKeycloakSecurityContext()
                                        .getIdToken()
                                        .getGivenName())
                        .withPrefilledCustomerFamilyName(
                                keycloakPrincipal
                                        .getKeycloakSecurityContext()
                                        .getIdToken()
                                        .getFamilyName())
                        .withScheme(RedirectFlowService.RedirectFlowCreateRequest.Scheme.BACS)
                        .execute();

        request.getSession()
                .setAttribute(
                        SESSION_NUMBER_OF_PAYMENTS,
                        Integer.parseInt(request.getParameter("number_payments")));
        request.getSession()
                .setAttribute(
                        SESSION_DAY_OF_MONTH,
                        Integer.parseInt(request.getParameter("payment_day")));
        request.getSession().setAttribute(SESSION_FLOW_ID, redirectFlow.getId());

        return new ModelAndView(format(REDIRECT_FORMAT, redirectFlow.getRedirectUrl()));
    }

    @Override
    public ModelAndView execute(HttpServletRequest request, Order order) {
        int dayOfMonth = (int) request.getSession().getAttribute(SESSION_DAY_OF_MONTH);
        int numberOfPayments = (int) request.getSession().getAttribute(SESSION_NUMBER_OF_PAYMENTS);
        String flowId = (String) request.getSession().getAttribute(SESSION_FLOW_ID);
        List<PaymentSchedule> schedules =
                (List<PaymentSchedule>) request.getSession().getAttribute(SESSION_SCHEDULES);

        PaymentSchedule paymentSchedule =
                schedules.stream()
                        .filter(ps -> ps.getNumberOfPayments() == numberOfPayments)
                        .findFirst()
                        .orElseThrow();

        RedirectFlow redirectFlow =
                client.redirectFlows()
                        .complete(flowId)
                        .withSessionToken(request.getRequestedSessionId())
                        .execute();
        Mandate mandate = client.mandates().get(redirectFlow.getLinks().getMandate()).execute();
        Customer customer = client.customers().get(redirectFlow.getLinks().getCustomer()).execute();

        List<LocalDate> chargeDates = calculateDates(mandate, dayOfMonth, numberOfPayments);
        LOG.info(
                "Payment dates = {}",
                String.join(
                        ", ",
                        chargeDates.stream()
                                .map(localDate -> localDate.toString())
                                .collect(Collectors.toList())));

        BigDecimal remaining = order.getTotal();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        for (LocalDate chargeDate : chargeDates) {
            BigDecimal chargeAmount = paymentSchedule.getAmount();
            if (remaining.compareTo(chargeAmount) < 0) {
                chargeAmount = paymentSchedule.getFinalAmount();
            }
            Payment payment =
                    client.payments()
                            .create()
                            .withAmount(chargeAmount.multiply(BigDecimal.valueOf(100)).intValue())
                            .withDescription(order.getWebReference())
                            .withIdempotencyKey(UUID.randomUUID().toString())
                            .withChargeDate(chargeDate.format(formatter))
                            .withLinksMandate(mandate.getId())
                            .withCurrency(GBP)
                            .execute();

            membershipService.createPayment(
                    order,
                    SERVICE_NAME,
                    payment.getId(),
                    LocalDate.parse(payment.getChargeDate()).atStartOfDay(),
                    chargeAmount,
                    BigDecimal.ZERO,
                    false,
                    false,
                    request.getUserPrincipal());
            remaining = remaining.subtract(chargeAmount);
        }

        return new ModelAndView(format("redirect:/payments/{0}/confirmation", SERVICE_NAME));
    }

    public List<LocalDate> calculateDates(Mandate mandate, int dayOfMonth, int numberOfPayments) {
        List<LocalDate> chargeDates = new ArrayList<>();
        LocalDate earliestDate = LocalDate.parse(mandate.getNextPossibleChargeDate());
        LocalDate startDate = earliestDate;

        if (dayOfMonth == -1) {
            startDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        } else {
            startDate = startDate.withDayOfMonth(dayOfMonth);
        }

        if (startDate.isBefore(earliestDate)) {
            startDate = startDate.plusMonths(1);
        }

        for (int i = 1; i <= numberOfPayments; i++) {
            chargeDates.add(startDate);
            startDate = startDate.plusMonths(1);
        }
        return chargeDates;
    }

    @Override
    public ModelAndView confirm(HttpServletRequest request, Order order) {
        return new ModelAndView("payments/gocardless/confirmation");
    }

    @Override
    public ModelAndView cancel(HttpServletRequest request, Order order) {
        return null;
    }
}
