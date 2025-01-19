package cricket.merstham.website.frontend.service.payment;

import com.apollographql.apollo.api.Error;
import com.gocardless.GoCardlessClient;
import com.gocardless.resources.CustomerBankAccount;
import com.gocardless.resources.Mandate;
import com.gocardless.services.RedirectFlowService;
import cricket.merstham.shared.dto.Order;
import cricket.merstham.website.frontend.exception.GraphException;
import cricket.merstham.website.frontend.model.RegistrationBasket;
import cricket.merstham.website.frontend.model.payment.PaymentSchedule;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.MembershipService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.gocardless.GoCardlessClient.Environment.LIVE;
import static com.gocardless.GoCardlessClient.Environment.SANDBOX;
import static com.gocardless.services.PaymentService.PaymentCreateRequest.Currency.GBP;
import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service("gocardless")
public class GoCardlessService implements PaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(GoCardlessService.class);

    private static final String SERVICE_NAME = "gocardless";
    private static final String REDIRECT_FORMAT = "redirect:{0}";
    private static final String SESSION_NUMBER_OF_PAYMENTS = "gc-number-of-payments";
    private static final String SESSION_DAY_OF_MONTH = "gc-day-of-month";
    private static final String SESSION_MANDATE = "gc-mandate";
    private static final String SESSION_SCHEDULES = "gc-schedules";
    private static final String SESSION_FLOW_ID = "gc-flow-id";
    public static final String MANDATE = "mandate";
    public static final String NEW = "new";

    private final boolean enabled;
    private final String disabledReason;
    private final MembershipService membershipService;
    private final String mandateDescription;
    private final LocalDate endDate;

    private final GoCardlessClient client;

    public GoCardlessService(
            @Value("${payments.gocardless.enabled}") boolean enabled,
            @Value("${payments.gocardless.disabled-reason}") String disabledReason,
            @Value("${payments.gocardless.access-token}") String accessToken,
            @Value("${payments.gocardless.sandbox}") boolean sandbox,
            @Value("${payments.gocardless.mandate-description}") String mandateDescription,
            @Value("${payments.gocardless.schedule-end-date}") LocalDate endDate,
            MembershipService membershipService) {
        this.enabled = enabled;
        this.disabledReason = disabledReason;
        this.membershipService = membershipService;
        this.mandateDescription = mandateDescription;
        this.endDate = endDate;
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
    public ModelAndView checkout(
            HttpServletRequest request, RegistrationBasket basket, OAuth2AccessToken accessToken) {
        var model = new HashMap<String, Object>();
        List<PaymentSchedule> schedules = new ArrayList<>();
        for (var i = 1; i <= 10; i++) {
            BigDecimal monthly =
                    basket.getBasketTotal().divide(BigDecimal.valueOf(i), 2, RoundingMode.UP);
            schedules.add(
                    new PaymentSchedule()
                            .setNumberOfPayments(i)
                            .setAmount(monthly)
                            .setFinalAmount(
                                    basket.getBasketTotal()
                                            .subtract(
                                                    monthly.multiply(
                                                            BigDecimal.valueOf((long) i - 1)))));
        }

        request.getSession().setAttribute(SESSION_SCHEDULES, schedules);
        model.put("schedules", schedules);
        model.put("endDate", endDate);
        try {
            var paymentMethods =
                    membershipService.getUsersPaymentMethods(
                            ((CognitoAuthentication) request.getUserPrincipal())
                                    .getOidcUser()
                                    .getSubject(),
                            accessToken);

            var existingMandates =
                    paymentMethods.stream()
                            .filter(
                                    p ->
                                            p.getType().equals(MANDATE)
                                                    && p.getStatus().equals("active")
                                                    && p.getProvider().equals(SERVICE_NAME))
                            .map(
                                    p ->
                                            MandatePresentation.builder()
                                                    .mandate(
                                                            client.mandates()
                                                                    .get(p.getMethodIdentifier())
                                                                    .execute()))
                            .filter(
                                    mandate ->
                                            mandate.mandate
                                                    .getStatus()
                                                    .name()
                                                    .equals(Mandate.Status.ACTIVE.name()))
                            .map(
                                    m ->
                                            m.customerBankAccount(
                                                            client.customerBankAccounts()
                                                                    .get(
                                                                            m.mandate
                                                                                    .getLinks()
                                                                                    .getCustomerBankAccount())
                                                                    .execute())
                                                    .build())
                            .toList();

            model.put("existingMandates", existingMandates);
        } catch (IOException e) {
            throw new RuntimeException("Error fetching active mandates", e);
        }

        return new ModelAndView("payments/gocardless/checkout", model);
    }

    @Override
    public ModelAndView authorise(
            HttpServletRequest request, RegistrationBasket basket, OAuth2AccessToken accessToken) {
        request.getSession()
                .setAttribute(
                        SESSION_NUMBER_OF_PAYMENTS,
                        Integer.parseInt(request.getParameter("number_payments")));
        request.getSession()
                .setAttribute(
                        SESSION_DAY_OF_MONTH,
                        Integer.parseInt(request.getParameter("payment_day")));

        var mandate = request.getParameter(MANDATE);
        if (isNull(mandate) || mandate.equals(NEW)) {
            var requestUri = URI.create(request.getRequestURL().toString());
            String baseUri = format("{0}://{1}", requestUri.getScheme(), requestUri.getAuthority());

            var principal = ((CognitoAuthentication) request.getUserPrincipal()).getOidcUser();

            var redirectFlow =
                    client.redirectFlows()
                            .create()
                            .withDescription(mandateDescription)
                            .withIdempotencyKey(UUID.randomUUID().toString())
                            .withSessionToken(basket.getId())
                            .withSuccessRedirectUrl(
                                    format("{0}/payments/{1}/execute", baseUri, SERVICE_NAME))
                            .withPrefilledCustomerEmail(request.getUserPrincipal().getName())
                            .withPrefilledCustomerGivenName(principal.getGivenName())
                            .withPrefilledCustomerFamilyName(principal.getFamilyName())
                            .withScheme(RedirectFlowService.RedirectFlowCreateRequest.Scheme.BACS)
                            .execute();

            request.getSession().setAttribute(SESSION_FLOW_ID, redirectFlow.getId());

            return new ModelAndView(format(REDIRECT_FORMAT, redirectFlow.getRedirectUrl()));
        }
        request.getSession().setAttribute(SESSION_MANDATE, mandate);

        return new ModelAndView(
                format(REDIRECT_FORMAT, format("/payments/{0}/execute", SERVICE_NAME)));
    }

    @Override
    public ModelAndView execute(
            HttpServletRequest request,
            RegistrationBasket basket,
            Order order,
            OAuth2AccessToken accessToken) {
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
        String mandateId;
        Mandate mandate;
        if (nonNull(flowId)) {
            var redirectFlow =
                    client.redirectFlows()
                            .complete(flowId)
                            .withSessionToken(basket.getId())
                            .execute();

            mandateId = redirectFlow.getLinks().getMandate();
            mandate = client.mandates().get(mandateId).execute();
            LOG.info("Saving mandate {}", mandate.getId());
            try {
                membershipService.createUserPaymentMethod(
                        SERVICE_NAME,
                        MANDATE,
                        mandate.getId(),
                        mandate.getLinks().getCustomer(),
                        "pending",
                        accessToken);
            } catch (GraphException e) {
                LOG.atError()
                        .setCause(e)
                        .log(
                                "Error saving mandate {} - '{}'",
                                mandate.getId(),
                                String.join(
                                        ", ",
                                        e.getErrors().stream().map(Error::getMessage).toList()),
                                e);
            } catch (Exception e) {
                LOG.atError()
                        .setCause(e)
                        .log("Unable to save payment method for mandate {}", mandate.getId());
            }
        } else {
            mandateId = (String) request.getSession().getAttribute(SESSION_MANDATE);
            mandate = client.mandates().get(mandateId).execute();
        }

        List<LocalDate> chargeDates = calculateDates(mandate, dayOfMonth, numberOfPayments);
        LOG.info(
                "Payment dates = {}",
                chargeDates.stream().map(LocalDate::toString).collect(Collectors.joining(", ")));

        BigDecimal remaining = basket.getBasketTotal();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        for (LocalDate chargeDate : chargeDates) {
            BigDecimal chargeAmount = paymentSchedule.getAmount();
            if (remaining.compareTo(chargeAmount) < 0) {
                chargeAmount = paymentSchedule.getFinalAmount();
            }
            var payment =
                    client.payments()
                            .create()
                            .withAmount(chargeAmount.multiply(BigDecimal.valueOf(100)).intValue())
                            .withDescription(order.getWebReference())
                            .withIdempotencyKey(UUID.randomUUID().toString())
                            .withChargeDate(chargeDate.format(formatter))
                            .withLinksMandate(mandate.getId())
                            .withRetryIfPossible(true)
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
                    "scheduled",
                    accessToken);
            remaining = remaining.subtract(chargeAmount);
        }

        return new ModelAndView(format("redirect:/payments/{0}/confirmation", SERVICE_NAME));
    }

    @Override
    public ModelAndView confirm(
            HttpServletRequest request, Order order, OAuth2AccessToken accessToken) {
        return new ModelAndView("payments/gocardless/confirmation", Map.of("order", order));
    }

    @Override
    public ModelAndView cancel(
            HttpServletRequest request, RegistrationBasket basket, OAuth2AccessToken accessToken) {
        return null;
    }

    private List<LocalDate> calculateDates(Mandate mandate, int dayOfMonth, int numberOfPayments) {
        List<LocalDate> chargeDates = new ArrayList<>();
        var earliestDate = LocalDate.parse(mandate.getNextPossibleChargeDate());
        var startDate = earliestDate;

        if (dayOfMonth == -1) {
            startDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        } else {
            startDate = startDate.withDayOfMonth(dayOfMonth);
        }

        if (startDate.isBefore(earliestDate)) {
            startDate = startDate.plusMonths(1);
        }

        for (var i = 1; i <= numberOfPayments; i++) {
            chargeDates.add(startDate);
            startDate = startDate.plusMonths(1);
        }
        return chargeDates;
    }

    @Builder
    @Data
    public static class MandatePresentation {
        private Mandate mandate;
        private CustomerBankAccount customerBankAccount;
    }
}
