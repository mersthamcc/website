package cricket.merstham.website.frontend.service.contact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PhoneTest {

    @Test
    void getKeyReturnsStaticValue() {
        var phone = new Phone(true, "GB");
        assertThat(phone.getKey()).isEqualTo("PHONE");
    }

    @Test
    void isEnabledReflectsConstructorParameter() {
        var phone = new Phone(true, "GB");
        assertThat(phone.isEnabled()).isTrue();
        phone = new Phone(false, "GB");
        assertThat(phone.isEnabled()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("validateTests")
    void shouldValidateCorrectly(String phoneNumber, List<String> expectResult) {
        var phone = new Phone(true, "GB");

        assertThat(phone.validate(phoneNumber)).isEqualTo(expectResult);
    }

    private static Stream<Arguments> validateTests() {
        return Stream.of(
                Arguments.of("07711 123456", List.of()),
                Arguments.of("07711 123", List.of("contact.PHONE.invalid")),
                Arguments.of("07711123456789", List.of("contact.PHONE.invalid")),
                Arguments.of("01737123456", List.of()),
                Arguments.of("(0771)1123456", List.of()),
                Arguments.of("+91 (99876) 90000", List.of()),
                Arguments.of("invalid-number", List.of("contact.PHONE.parse-error")));
    }
}
