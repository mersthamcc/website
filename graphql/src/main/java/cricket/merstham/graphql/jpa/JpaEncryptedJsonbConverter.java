package cricket.merstham.graphql.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Converter
public class JpaEncryptedJsonbConverter extends JpaEncryptedBaseConverter
        implements AttributeConverter<JsonNode, String> {

    public JpaEncryptedJsonbConverter(
            ObjectMapper objectMapper, @Value("${configuration.database-secret}") String secret) {
        super(objectMapper, secret);
    }

    @Override
    public String convertToDatabaseColumn(JsonNode attribute) {
        return wrap(
                attribute,
                jsonNode -> {
                    try {
                        return mapper.writeValueAsBytes(attribute);
                    } catch (JsonProcessingException ex) {
                        throw new RuntimeException(
                                "Failed to serialize object to JSON: " + ex.getMessage(), ex);
                    }
                });
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbData) {
        return unwrap(
                dbData,
                (bytes) -> {
                    try {
                        return mapper.readTree(bytes);
                    } catch (IOException ex) {
                        throw new RuntimeException("Failed to parse JSON: " + ex.getMessage(), ex);
                    }
                });
    }
}
