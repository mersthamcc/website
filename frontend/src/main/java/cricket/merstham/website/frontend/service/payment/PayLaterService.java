package cricket.merstham.website.frontend.service.payment;

import cricket.merstham.website.frontend.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

import static java.text.MessageFormat.format;

@Service("pay-later")
public class PayLaterService implements PaymentService {

    private static final String SERVICE_NAME = "pay-later";
    private final boolean enabled;
    private final String disabledReason;

    public PayLaterService(
            @Value("${payments.pay-later.enabled}") boolean enabled,
            @Value("${payments.pay-later.disabled-reason}") String disabledReason) {
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
    public Optional<String> getDisabledReason() {
        return Optional.ofNullable(disabledReason);
    }

    @Override
    public ModelAndView checkout(
            HttpServletRequest request, Order order, OAuth2AccessToken accessToken) {
        return new ModelAndView(format("redirect:/payments/{0}/confirmation", SERVICE_NAME));
    }

    @Override
    public ModelAndView authorise(
            HttpServletRequest request, Order order, OAuth2AccessToken accessToken) {
        return null;
    }

    @Override
    public ModelAndView execute(
            HttpServletRequest request, Order order, OAuth2AccessToken accessToken) {
        return null;
    }

    @Override
    public ModelAndView confirm(
            HttpServletRequest request, Order order, OAuth2AccessToken accessToken) {
        return new ModelAndView("payments/pay-later/confirmation");
    }

    @Override
    public ModelAndView cancel(
            HttpServletRequest request, Order order, OAuth2AccessToken accessToken) {
        return null;
    }
}
