package cricket.merstham.website.frontend.helpers;

import org.springframework.web.servlet.view.RedirectView;

public class RedirectHelper {
    public static RedirectView redirectTo(String url) {
        return new RedirectView(url, false, true, false);
    }
}
