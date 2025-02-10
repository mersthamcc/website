package cricket.merstham.website.frontend.service.payment;

import cricket.merstham.shared.dto.Order;
import cricket.merstham.website.frontend.model.RegistrationBasket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.text.MessageFormat.format;

@Service("bank")
public class BankTransferService implements PaymentService {

    private static final String SERVICE_NAME = "bank";
    private final boolean enabled;
    private final String disabledReason;
    private final String bankAccountName;
    private final String bankAccountSortCode;
    private final String bankAccountNumber;

    public BankTransferService(
            @Value("${payments.bank.enabled}") boolean enabled,
            @Value("${payments.bank.disabled-reason}") String disabledReason,
            @Value("${payments.bank.account-name}") String bankAccountName,
            @Value("${payments.bank.sort-code}") String bankAccountSortCode,
            @Value("${payments.bank.account-number}") String bankAccountNumber) {
        this.enabled = enabled;
        this.disabledReason = disabledReason;
        this.bankAccountName = bankAccountName;
        this.bankAccountSortCode = bankAccountSortCode;
        this.bankAccountNumber = bankAccountNumber;
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
        return new ModelAndView(format("redirect:/payments/{0}/execute", SERVICE_NAME));
    }

    @Override
    public ModelAndView authorise(
            HttpServletRequest request, RegistrationBasket basket, OAuth2AccessToken accessToken) {
        return null;
    }

    @Override
    public ModelAndView execute(
            HttpServletRequest request,
            RegistrationBasket basket,
            Order order,
            OAuth2AccessToken accessToken) {
        return new ModelAndView(format("redirect:/payments/{0}/confirmation", SERVICE_NAME));
    }

    @Override
    public ModelAndView confirm(
            HttpServletRequest request, Order order, OAuth2AccessToken accessToken) {
        return new ModelAndView(
                "payments/bank/confirmation",
                Map.of(
                        "bankAccountName", bankAccountName,
                        "bankAccountNumber", bankAccountNumber,
                        "bankAccountSortCode", bankAccountSortCode,
                        "orderReference", order.getWebReference()));
    }

    @Override
    public ModelAndView cancel(
            HttpServletRequest request, RegistrationBasket basket, OAuth2AccessToken accessToken) {
        return null;
    }

    @Override
    public Map<String, Object> getEmailModel(Map<String, Object> baseModel) {
        var model = new HashMap<>(baseModel);
        model.putAll(
                Map.of(
                        "bankAccountName", bankAccountName,
                        "bankAccountNumber", bankAccountNumber,
                        "bankAccountSortCode", bankAccountSortCode));
        return model;
    }
}
