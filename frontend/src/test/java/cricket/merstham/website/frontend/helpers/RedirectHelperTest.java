package cricket.merstham.website.frontend.helpers;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.RedirectView;

import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;
import static org.assertj.core.api.Assertions.assertThat;

class RedirectHelperTest {

    @Test
    void shouldContainCorrectRedirectView() {
        var redirect = redirectTo("/");
        assertThat(redirect)
                .matches(RedirectView::isRedirectView)
                .matches(redirectView -> !redirectView.isPropagateQueryProperties())
                .extracting(AbstractUrlBasedView::getUrl)
                .isEqualTo("/");
    }
}
