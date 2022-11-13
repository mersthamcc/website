package cricket.merstham.website.frontend.service.payment;

import cricket.merstham.shared.dto.Order;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface PaymentService {
    String getName();

    boolean isEnabled();

    Optional<String> getDisabledReason();

    ModelAndView checkout(HttpServletRequest request, Order order, OAuth2AccessToken accessToken);

    ModelAndView authorise(HttpServletRequest request, Order order, OAuth2AccessToken accessToken);

    ModelAndView execute(HttpServletRequest request, Order order, OAuth2AccessToken accessToken);

    ModelAndView confirm(HttpServletRequest request, Order order, OAuth2AccessToken accessToken);

    ModelAndView cancel(HttpServletRequest request, Order order, OAuth2AccessToken accessToken);
}
