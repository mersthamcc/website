package cricket.merstham.graphql.jpa;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

import java.io.StringWriter;
import java.util.List;

import static java.util.Objects.isNull;

public class JpaJsonbToListConverter<T> implements AttributeConverter<List<T>, String> {

    protected final ObjectMapper mapper;

    public JpaJsonbToListConverter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String convertToDatabaseColumn(List<T> attribute) {
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
    public List<T> convertToEntityAttribute(String dbData) {
        if (isNull(dbData)) {
            return null;
        }
        try {
            return mapper.readValue(dbData, new TypeReference<>() {});
        } catch (final Exception ex) {
            throw new RuntimeException("Failed to parse JSON: " + ex.getMessage(), ex);
        }
    }
}
