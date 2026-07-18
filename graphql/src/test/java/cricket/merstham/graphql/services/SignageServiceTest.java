package cricket.merstham.graphql.services;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.api.client.util.DateTime;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SignageServiceTest {

    private final SignageService service = new SignageService("", "", 0, 0, 0, new JsonMapper());

    @Test
    void convertToLocalDateTimeInDst() {
        var source = new DateTime("2026-07-18T11:00:00Z");
        var result = service.convertToLocalDateTime(source);

        assertThat(result).isEqualTo("2026-07-18T11:55:00");
    }

    @Test
    void convertToLocalDateTimeNotInDst() {
        var source = new DateTime("2026-01-18T11:00:00Z");
        var result = service.convertToLocalDateTime(source);

        assertThat(result).isEqualTo("2026-01-18T10:55:00");
    }
}
