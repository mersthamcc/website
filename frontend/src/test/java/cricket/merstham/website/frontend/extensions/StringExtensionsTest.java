package cricket.merstham.website.frontend.extensions;

import org.junit.jupiter.api.Test;

import static cricket.merstham.shared.extensions.StringExtensions.toSlug;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringExtensionsTest {

    @Test
    void shouldCorrectlyConvertStringsToExpectedSlugs() {
        assertThat(toSlug("Hello World"), equalTo("hello-world"));
        assertThat(toSlug("Hello World!"), equalTo("hello-world"));
        assertThat(toSlug("Hello-World"), equalTo("hello-world"));
        assertThat(toSlug("Hello,- World!"), equalTo("hello-world"));
        assertThat(toSlug("Hello 2 Worlds"), equalTo("hello-2-worlds"));
        assertThat(
                toSlug("This is  a   really long title with all  sorts of 32&^345''' // in it!"),
                equalTo("this-is-a-really-long-title-with-all-sorts-of-32-345-in-it"));
    }

    @Test
    void shouldThrowWithInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> toSlug(null));
        assertThrows(IllegalArgumentException.class, () -> toSlug(""));
        assertThrows(IllegalArgumentException.class, () -> toSlug("        "));
    }
}
