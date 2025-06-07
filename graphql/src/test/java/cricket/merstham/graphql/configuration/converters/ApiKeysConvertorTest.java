package cricket.merstham.graphql.configuration.converters;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionFailedException;

import static java.text.MessageFormat.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

class ApiKeysConvertorTest {

    private static final String API_KEY_ONE =
            """
            {
                "name": "one",
                "key": "key-one",
                "trusted": true
            }
            """;
    private static final String API_KEY_TWO =
            """
            {
                "name": "two",
                "key": "key-two",
                "trusted": false
            }
            """;
    private ApiKeysConvertor convertor = new ApiKeysConvertor();

    @Test
    void shouldThrowExceptionWhenInputIsNull() {
        assertThrows(IllegalArgumentException.class, () -> convertor.convert(null));
    }

    @Test
    void shouldConvertEmptyArray() {
        var result = convertor.convert("[]");
        assertThat(result).hasSize(0);
    }

    @Test
    void shouldThrowExceptionWhenInputIsEmpty() {
        assertThrows(ConversionFailedException.class, () -> convertor.convert(""));
    }

    @Test
    void shouldThrowExceptionWhenInputIsInvalidJsonString() {
        assertThrows(
                ConversionFailedException.class, () -> convertor.convert("[{\"key\":\"value\"}"));
    }

    @Test
    void shouldConvertSingleElementArray() {
        var result = convertor.convert(format("[ {0} ]", API_KEY_ONE));
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("one");
        assertThat(result.get(0).getKey()).isEqualTo("key-one");
        assertThat(result.get(0).isTrusted()).isTrue();
    }

    @Test
    void shouldConvertMultipleElementArray() {
        var result = convertor.convert(format("[ {0}, {1} ]", API_KEY_ONE, API_KEY_TWO));
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("one");
        assertThat(result.get(0).getKey()).isEqualTo("key-one");
        assertThat(result.get(0).isTrusted()).isTrue();

        assertThat(result.get(1).getName()).isEqualTo("two");
        assertThat(result.get(1).getKey()).isEqualTo("key-two");
        assertThat(result.get(1).isTrusted()).isFalse();
    }
}
