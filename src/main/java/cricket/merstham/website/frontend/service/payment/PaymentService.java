package cricket.merstham.website.frontend.service.payment;

import cricket.merstham.website.frontend.model.Order;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface PaymentService {
    String getName();

    boolean isEnabled();

    Optional<String> getDisabledReason();

    ModelAndView checkout(HttpServletRequest request, Order order);

    ModelAndView authorise(HttpServletRequest request, Order order);

    ModelAndView execute(HttpServletRequest request, Order order);

    ModelAndView confirm(HttpServletRequest request, Order order);

    ModelAndView cancel(HttpServletRequest request, Order order);
}
