package cricket.merstham.website.frontend.service.payment;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {
    String getName();
    boolean isEnabled();
    Optional<String> getDisabledReason();
    ModelAndView information(UUID uuid, BigDecimal bigDecimal);
    ModelAndView execute(HttpServletRequest request);
    ModelAndView complete(HttpServletRequest request);
}
