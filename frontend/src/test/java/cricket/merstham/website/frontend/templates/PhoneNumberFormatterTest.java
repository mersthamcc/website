package cricket.merstham.website.frontend.templates;

import freemarker.template.SimpleHash;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.assertj.core.api.InstanceOfAssertFactory;
import org.assertj.core.api.MapAssert;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.InstanceOfAssertFactories.map;

class PhoneNumberFormatterTest {

    private final PhoneNumberFormatter formatter = new PhoneNumberFormatter();

    public static Stream<Arguments> happyScenarios() {
        return Stream.of(
                Arguments.of("01737 643456", "+44", "1737", "643456", "01737 643456"),
                Arguments.of("(01737) 643456", "+44", "1737", "643456", "01737 643456"),
                Arguments.of("01737643456", "+44", "1737", "643456", "01737 643456"),
                Arguments.of("02081234567", "+44", "20", "81234567", "020 8123 4567"),
                Arguments.of("07711 123456", "+44", "7711", "123456", "07711 123456"),
                Arguments.of("07711123456", "+44", "7711", "123456", "07711 123456"),
                Arguments.of("(0771)1123456", "+44", "7711", "123456", "07711 123456"),
                Arguments.of("+91 (99876) 90000", "+91", "99876", "90000", "+91 99876 90000"),
                Arguments.of("+919987690000", "+91", "99876", "90000", "+91 99876 90000"),
                Arguments.of("+91-99876-90000", "+91", "99876", "90000", "+91 99876 90000"),
                Arguments.of("+91 99876 90000", "+91", "99876", "90000", "+91 99876 90000"));
    }

    @ParameterizedTest
    @MethodSource("happyScenarios")
    void shouldReturnCorrectlyFormattedItemForUKLandlineNumber(
            String input,
            String countryCode,
            String areaCode,
            String localPart,
            String formatted) throws TemplateModelException {
        var result = formatter.exec(List.of(input));

//        assertThat(result)
//                .isInstanceOf(SimpleHash.class)
//                .extracting(PhoneNumberFormatterTest::extractMap, asStringMap())
//                .containsExactlyInAnyOrderEntriesOf(
//                        Map.of(
//                                "country_code", countryCode,
//                                "area_code", areaCode,
//                                "local_number", localPart,
//                                "formatted", formatted));
    }

    private static InstanceOfAssertFactory<Map, MapAssert<String, String>> asStringMap() {
        return as(map(String.class, String.class));
    }

    @NotNull
    private static Map extractMap(TemplateModel templateModel) {
        try {
            return ((SimpleHash) templateModel).toMap();
        } catch (TemplateModelException e) {
            throw new RuntimeException(e);
        }
    }
}
