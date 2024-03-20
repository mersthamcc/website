package cricket.merstham.graphql.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JpaJsonbToListConverterTest {

    private ObjectMapper objectMapper = new JsonMapper();

    @Test
    void convertToDatabaseColumnForStringLists() {
        var converter = new JpaJsonbToListConverter<String>(objectMapper);
        var sourceData = List.of("One", "Two", "Three");

        var result = converter.convertToDatabaseColumn(sourceData);

        assertThat(result).isEqualTo("[\"One\",\"Two\",\"Three\"]");
    }

    @Test
    void convertToDatabaseColumnForIntegerLists() {
        var converter = new JpaJsonbToListConverter<Integer>(objectMapper);
        var sourceData = List.of(1, 2, 3);

        var result = converter.convertToDatabaseColumn(sourceData);

        assertThat(result).isEqualTo("[1,2,3]");
    }

    @Test
    void convertToDatabaseColumnForStringListsWhenEmpty() {
        var converter = new JpaJsonbToListConverter<String>(objectMapper);
        var sourceData = List.<String>of();

        var result = converter.convertToDatabaseColumn(sourceData);

        assertThat(result).isEqualTo("[]");
    }

    @Test
    void convertToDatabaseColumnForStringListsWhenNull() {
        var converter = new JpaJsonbToListConverter<String>(objectMapper);

        var result = converter.convertToDatabaseColumn(null);

        assertThat(result).isNull();
    }

    @Test
    void convertToEntityAttributeForStringLists() {
        var converter = new JpaJsonbToListConverter<String>(objectMapper);
        var sourceData = "[\"One\",\"Two\",\"Three\"]";

        var result = converter.convertToEntityAttribute(sourceData);

        assertThat(result).hasSize(3).containsExactly("One", "Two", "Three");
    }

    @Test
    void convertToEntityAttributeForIntegerLists() {
        var converter = new JpaJsonbToListConverter<Integer>(objectMapper);
        var sourceData = "[1,2,3]";

        var result = converter.convertToEntityAttribute(sourceData);

        assertThat(result).hasSize(3).containsExactly(1, 2, 3);
    }

    @Test
    void convertToEntityAttributeForStringListsWhenEmpty() {
        var converter = new JpaJsonbToListConverter<String>(objectMapper);
        var sourceData = "[]";

        var result = converter.convertToEntityAttribute(sourceData);

        assertThat(result).isEmpty();
    }

    @Test
    void convertToEntityAttributeForStringListsWhenNull() {
        var converter = new JpaJsonbToListConverter<String>(objectMapper);

        var result = converter.convertToEntityAttribute(null);

        assertThat(result).isNull();
    }
}
