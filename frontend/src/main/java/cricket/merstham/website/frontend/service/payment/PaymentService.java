package cricket.merstham.website.frontend.service.payment;

import cricket.merstham.shared.dto.Order;
import cricket.merstham.website.frontend.model.RegistrationBasket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.Optional;

import static java.text.MessageFormat.format;

public interface PaymentService {
    String getName();

    boolean isEnabled();

    Optional<String> getDisabledReason();

    ModelAndView checkout(
            HttpServletRequest request, RegistrationBasket basket, OAuth2AccessToken accessToken);

    ModelAndView authorise(
            HttpServletRequest request, RegistrationBasket basket, OAuth2AccessToken accessToken);

    ModelAndView execute(
            HttpServletRequest request,
            RegistrationBasket basket,
            Order order,
            OAuth2AccessToken accessToken);

    ModelAndView confirm(HttpServletRequest request, Order order, OAuth2AccessToken accessToken);

    ModelAndView cancel(
            HttpServletRequest request, RegistrationBasket basket, OAuth2AccessToken accessToken);

    default String getConfirmationEmail() {
        return format("payments/{0}/confirmation-email.ftl", getName());
    }
    ;

    default Map<String, Object> getEmailModel(Map<String, Object> baseModel) {
        return baseModel;
    }
    ;
}
