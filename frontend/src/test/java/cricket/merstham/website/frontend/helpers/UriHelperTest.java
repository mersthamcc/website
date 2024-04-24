package cricket.merstham.website.frontend.helpers;

import org.junit.jupiter.api.Test;

import static cricket.merstham.website.frontend.helpers.UriHelper.resolveUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UriHelperTest {

    @Test
    void resolveUrlTest() {
        assertThat(resolveUrl("http://localhost/", "/test")).isEqualTo("http://localhost/test");
        assertThat(resolveUrl("http://localhost/", "http://anotherhost/test"))
                .isEqualTo("http://anotherhost/test");
        assertThat(
                        resolveUrl(
                                "http://localhost/",
                                "data:image/gif;base64,R0lGODdhMAAwAPAAAAAAAP///ywAAAAAMAAw"))
                .isEqualTo("data:image/gif;base64,R0lGODdhMAAwAPAAAAAAAP///ywAAAAAMAAw");
        assertThat(resolveUrl("http://localhost/", "")).isEqualTo("http://localhost/");
        assertThat(resolveUrl("http://localhost/", null)).isEqualTo("http://localhost/");
        assertThat(resolveUrl("http://localhost", "news")).isEqualTo("http://localhost/news");
    }
}
