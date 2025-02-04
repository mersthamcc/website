package cricket.merstham.website.frontend.interceptors;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import static java.util.Objects.nonNull;

public class PageViewInterceptor implements HandlerInterceptor {

    private final MeterRegistry meterRegistry;

    public PageViewInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {
        meterRegistry
                .counter(
                        "cricket.merstham.page.view",
                        "uri",
                        request.getRequestURI(),
                        "method",
                        request.getMethod(),
                        "status",
                        Integer.toString(response.getStatus()))
                .increment();
        var session = request.getSession(false);
        var sessionId = "anonymous";
        if (nonNull(session)) {
            try {
                sessionId = session.getId();
            } catch (NullPointerException | IllegalStateException ignored) {
                // session is invalid, use default value
            }
        }

        var remoteAddress = request.getHeader("X-Forwarded-For");
        meterRegistry
                .counter(
                        "cricket.merstham.page.session",
                        "uri",
                        request.getRequestURI(),
                        "remote_ip",
                        nonNull(remoteAddress) ? remoteAddress : request.getRemoteAddr(),
                        "session",
                        sessionId)
                .increment();
    }
}
