package cricket.merstham.website.frontend.security;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class EncryptedStringTest {

    @Test
    void decryptShouldReturnOriginalString() {
        var password = "a-random-password"; // pragma: allowlist secret
        var salt = "abcd1234";
        var plainText = "this is the original text";

        var encryptedString = new SealedString(plainText, password, salt);

        assertThat(encryptedString.decrypt(password, salt), equalTo(plainText));
    }
}
