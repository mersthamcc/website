package cricket.merstham.website.frontend.service.contact;

import cricket.merstham.website.frontend.service.EmailAddressValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EmailTest {
    @Test
    void getKeyReturnsStaticValue() {
        var email = new Email(true, new EmailAddressValidator());
        assertThat(email.getKey()).isEqualTo("EMAIL");
    }

    @Test
    void isEnabledReflectsConstructorParameter() {
        var email = new Email(true, new EmailAddressValidator());
        assertThat(email.isEnabled()).isTrue();
        email = new Email(false, new EmailAddressValidator());
        assertThat(email.isEnabled()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("validateTests")
    void shouldValidateCorrectly(String emailAddress, List<String> expectResult) {
        var email = new Email(true, new EmailAddressValidator());

        assertThat(email.validate(emailAddress)).isEqualTo(expectResult);
    }

    private static Stream<Arguments> validateTests() {
        return Stream.of(
                Arguments.of("user@domain.com", List.of()),
                Arguments.of("user@domain.com.", List.of("contact.EMAIL.invalid")),
                Arguments.of(".user@domain.com", List.of("contact.EMAIL.invalid")),
                Arguments.of("firstname.surname@domain.co.uk", List.of()),
                Arguments.of("firstname.surname+unique@domain.co.uk", List.of()),
                Arguments.of("firstname.surname@domain.org", List.of()),
                Arguments.of("firstname.surname@gmail.com", List.of()),
                Arguments.of("firstname.surname@hotmail.com", List.of()),
                Arguments.of("firstname.surname@outlook.com", List.of()));
    }
}
