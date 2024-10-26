package cricket.merstham.website.frontend.controller;

import com.apollographql.apollo.api.Error;
import cricket.merstham.website.frontend.exception.GraphException;
import io.sentry.Sentry;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.stream.Collectors;

import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Controller
@ControllerAdvice
public class FrontEndErrorController
        implements org.springframework.boot.web.servlet.error.ErrorController {

    private static final Logger LOG = LoggerFactory.getLogger(FrontEndErrorController.class);
    public static final String EXCEPTION_FLASH = "exception";

    @GetMapping(value = "/error")
    public ModelAndView errorView(HttpServletRequest request) {
        var status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (nonNull(status) && status.equals(HttpStatus.NOT_FOUND.value())) {
            return new ModelAndView("error/404");
        }

        var exception = RequestContextUtils.getInputFlashMap(request);
        Map<String, Object> model =
                isNull(exception) ? Map.of() : Map.of(EXCEPTION_FLASH, exception);
        return new ModelAndView("error/500", model);
    }

    @GetMapping(value = "/throw-error", name = "throw-error")
    public ModelAndView throwError() {
        throw new NullPointerException();
    }

    @ExceptionHandler(RuntimeException.class)
    public RedirectView handleError(
            HttpServletRequest request,
            RuntimeException exception,
            RedirectAttributes redirectAttributes) {
        if (nonNull(AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class))
                || exception instanceof AccessDeniedException) {
            throw exception;
        }

        if (exception instanceof GraphException graphException) {
            LOG.atError()
                    .setMessage("GraphQL Errors: {}")
                    .addArgument(
                            () ->
                                    graphException.getErrors().stream()
                                            .map(Error::getMessage)
                                            .collect(Collectors.joining(", ")))
                    .setCause(graphException)
                    .log();
        } else {
            LOG.atError().setMessage("An unexpected error occurred").setCause(exception).log();
        }
        redirectAttributes.addFlashAttribute(EXCEPTION_FLASH, exception.getLocalizedMessage());
        Sentry.captureException(exception);
        return redirectTo("/error");
    }
}
