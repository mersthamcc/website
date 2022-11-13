package cricket.merstham.website.frontend.mappers;

import com.apollographql.apollo.api.CustomTypeAdapter;
import com.apollographql.apollo.api.CustomTypeValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JsonNodeCustomTypeAdapter implements CustomTypeAdapter<JsonNode> {
    private static final JsonNodeFactory JSON = JsonNodeFactory.instance;

    @Override
    public JsonNode decode(@NotNull CustomTypeValue<?> customTypeValue) {
        switch (customTypeValue.getClass().getSimpleName()) {
            case "GraphQLString":
                return JSON.textNode((String) customTypeValue.value);
            case "GraphQLJsonList":
                List<String> values = (List) customTypeValue.value;
                return JSON.arrayNode()
                        .addAll(values.stream().map(JSON::textNode).collect(Collectors.toList()));
            case "GraphQLBoolean":
                return JSON.booleanNode((Boolean) customTypeValue.value);
            case "GraphQLNumber":
                var value = (CustomTypeValue.GraphQLNumber) customTypeValue;
                return JSON.numberNode(value.value.longValue());
        }
        return JSON.nullNode();
    }

    @NotNull
    @Override
    public CustomTypeValue<?> encode(JsonNode jsonNode) {
        if (jsonNode.isArray()) {
            List<String> list = new ArrayList<>();
            for (JsonNode node : jsonNode) {
                list.add(node.asText());
            }
            return new CustomTypeValue.GraphQLJsonList(list);
        }
        if (jsonNode.isBoolean()) {
            return new CustomTypeValue.GraphQLBoolean(jsonNode.asBoolean());
        }
        if (jsonNode.isNumber()) {
            return new CustomTypeValue.GraphQLNumber(jsonNode.asLong());
        }
        return new CustomTypeValue.GraphQLString(jsonNode.asText());
    }
}
