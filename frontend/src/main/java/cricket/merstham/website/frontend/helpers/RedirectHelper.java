package cricket.merstham.website.frontend.helpers;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

public final class RedirectHelper {
    public static RedirectView redirectTo(String url) {
        return new RedirectView(url, false, true, false);
    }

    public static ModelAndView redirectToPage(String url) {
        return new ModelAndView("redirect:" + url);
    }

    private RedirectHelper() {}
}
