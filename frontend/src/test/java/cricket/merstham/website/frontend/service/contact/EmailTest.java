package cricket.merstham.website.frontend.service.contact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EmailTest {
    @Test
    void getKeyReturnsStaticValue() {
        var email = new Email(true);
        assertThat(email.getKey()).isEqualTo("EMAIL");
    }

    @Test
    void isEnabledReflectsConstructorParameter() {
        var email = new Email(true);
        assertThat(email.isEnabled()).isTrue();
        email = new Email(false);
        assertThat(email.isEnabled()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("validateTests")
    void shouldValidateCorrectly(String emailAddress, boolean expectResult) {
        var email = new Email(true);

        assertThat(email.validate(emailAddress)).isEqualTo(expectResult);
    }

    private static Stream<Arguments> validateTests() {
        return Stream.of(
                Arguments.of("user@domain.com", true),
                Arguments.of("user@domain.com.", false),
                Arguments.of(".user@domain.com", false),
                Arguments.of("firstname.surname@domain.co.uk", true),
                Arguments.of("firstname.surname+unique@domain.co.uk", true),
                Arguments.of("firstname.surname@domain.org", true),
                Arguments.of("firstname.surname@gmail.com", true),
                Arguments.of("firstname.surname@hotmail.com", true),
                Arguments.of("firstname.surname@outlook.com", true));
    }
}
