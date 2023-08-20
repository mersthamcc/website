package cricket.merstham.graphql.jpa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

import java.io.StringWriter;

import static java.util.Objects.isNull;

public class JpaJsonbConverter implements AttributeConverter<JsonNode, String> {

    protected final ObjectMapper mapper;

    public JpaJsonbConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String convertToDatabaseColumn(JsonNode attribute) {
        if (isNull(attribute)) {
            return null;
        }
        try {
            final StringWriter w = new StringWriter();
            mapper.writeValue(w, attribute);
            w.flush();
            return w.toString();
        } catch (final Exception ex) {
            throw new RuntimeException("Failed to convert to JSONB: " + ex.getMessage(), ex);
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbData) {
        if (isNull(dbData)) {
            return null;
        }
        try {
            return mapper.readTree(dbData);
        } catch (final Exception ex) {
            throw new RuntimeException("Failed to parse JSON: " + ex.getMessage(), ex);
        }
    }
}
