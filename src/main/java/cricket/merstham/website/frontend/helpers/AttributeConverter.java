package cricket.merstham.website.frontend.helpers;

import cricket.merstham.website.frontend.model.AttributeDefinition;
import cricket.merstham.website.graph.type.AttributeType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AttributeConverter {
    public static Object convert(Map<String, AttributeDefinition> attributes, String key, Object value) {
        for (var attr : attributes.entrySet()) {
            if (key.equals(attr.getKey())) {
                AttributeType type = AttributeType.safeValueOf(attr.getValue().getType());
                switch (type) {
                    case DATE:
                        var formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        try {
                            return formatter.parse((String) value);
                        } catch (ParseException e) {
                            throw new RuntimeException("Error parsing date string", e);
                        }
                    case BOOLEAN:
                        return Boolean.parseBoolean((String) value);
                    case NUMBER:
                        return Long.parseLong((String) value);
                    case LIST:
                        if (value instanceof String) return List.of(value);
                        return Arrays.asList((String[]) value);
                    default:
                        return value;
                }
            }
        }
        return null;
    }
}
