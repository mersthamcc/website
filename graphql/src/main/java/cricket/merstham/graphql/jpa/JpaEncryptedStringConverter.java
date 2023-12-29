package cricket.merstham.graphql.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;

@Converter
public class JpaEncryptedStringConverter extends JpaEncryptedBaseConverter
        implements AttributeConverter<String, String> {

    public JpaEncryptedStringConverter(
            ObjectMapper objectMapper, @Value("${configuration.database-secret}") String secret) {
        super(objectMapper, secret);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return wrap(attribute, s -> s.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return unwrap(dbData, String::new);
    }
}
