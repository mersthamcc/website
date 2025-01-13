package cricket.merstham.website.frontend.service.payment;

import cricket.merstham.shared.dto.Order;
import cricket.merstham.website.frontend.model.RegistrationBasket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

import static java.text.MessageFormat.format;

@Service("complementary")
public class ComplementaryPaymentService implements PaymentService {

    private static final String SERVICE_NAME = "complementary";
    private final boolean enabled;
    private final String disabledReason;

    public ComplementaryPaymentService(
            @Value("${payments.complementary.enabled}") boolean enabled,
            @Value("${payments.complementary.disabled-reason}") String disabledReason) {
        this.enabled = enabled;
        this.disabledReason = disabledReason;
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
    public boolean isHidden() {
        return true;
    }

    @Override
    public Optional<String> getDisabledReason() {
        return Optional.ofNullable(disabledReason);
    }

    @Override
    public ModelAndView checkout(
            HttpServletRequest request, RegistrationBasket basket, OAuth2AccessToken accessToken) {
        return new ModelAndView(format("redirect:/payments/{0}/confirmation", SERVICE_NAME));
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
        return null;
    }

    @Override
    public ModelAndView confirm(
            HttpServletRequest request, Order order, OAuth2AccessToken accessToken) {
        return new ModelAndView("payments/complementary/confirmation");
    }

    @Override
    public ModelAndView cancel(
            HttpServletRequest request, RegistrationBasket basket, OAuth2AccessToken accessToken) {
        return null;
    }
}
