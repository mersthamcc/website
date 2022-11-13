package cricket.merstham.website.frontend.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import cricket.merstham.shared.dto.AttributeDefinition;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.logging.log4j.util.Strings.isBlank;

public class AttributeConverter {

    private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

    public static JsonNode convert(AttributeDefinition attribute, List<Object> value) {
        if (isNull(value)) return JSON.nullNode();
        switch (attribute.getType()) {
            case Boolean:
                return JSON.booleanNode(Boolean.parseBoolean((String) value.get(0)));
            case Number:
                return JSON.numberNode(Long.parseLong((String) value.get(0)));
            case List:
                return JSON.arrayNode()
                        .addAll(
                                value.stream()
                                        .filter(v -> !v.equals("nothing-checked"))
                                        .map(o -> JSON.textNode((String) o))
                                        .collect(Collectors.toList()));
            default:
                return JSON.textNode((String) value.get(0));
        }
    }

    public static Object convert(AttributeDefinition attribute, JsonNode value, Locale locale) {
        var valueAsString = value.asText();
        switch (attribute.getType()) {
            case Date:
                var formatter = new SimpleDateFormat("yyyy-MM-dd", locale);
                try {
                    if (isBlank(valueAsString)) {
                        return "";
                    }
                    return formatter.parse(valueAsString);
                } catch (ParseException e) {
                    throw new RuntimeException("Error parsing date string", e);
                }
            case Boolean:
                return Boolean.parseBoolean(valueAsString);
            case Number:
                return Long.parseLong(valueAsString);
            case List:
                if (value.isArray()) {
                    var list = new ArrayList<String>();
                    for (var node : value) {
                        list.add(node.asText());
                    }
                    return list;
                }
                ;
                return List.of(valueAsString);
            default:
                return value.asText();
        }
    }
}
