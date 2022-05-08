package cricket.merstham.graphql.scalars;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import graphql.language.ArrayValue;
import graphql.language.BooleanValue;
import graphql.language.EnumValue;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.NullValue;
import graphql.language.ObjectField;
import graphql.language.ObjectValue;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.language.VariableReference;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.util.FpKit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static graphql.language.ObjectField.newObjectField;

public class JsonCoercing implements Coercing<JsonNode, Object> {

    private final JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    @Override
    public Object serialize(Object dataFetcherResult) throws CoercingSerializeException {
        return dataFetcherResult;
    }

    @Override
    public JsonNode parseValue(Object input) throws CoercingParseValueException {
        try {
            return objectMapper.readTree((String) input);
        } catch (JsonProcessingException e) {
            throw CoercingParseValueException.newCoercingParseValueException()
                    .cause(e)
                    .message("Error parsing value")
                    .build();
        }
    }

    @Override
    public JsonNode parseLiteral(Object input) throws CoercingParseLiteralException {
        return parseLiteral(input, Map.of());
    }

    @Override
    public JsonNode parseLiteral(Object input, Map<String, Object> variables)
            throws CoercingParseLiteralException {
        if (!(input instanceof Value)) {
            throw new CoercingParseLiteralException(
                    "Expected AST type 'Value' but was '"
                            + input.getClass().getCanonicalName()
                            + "'.");
        }

        if (input instanceof NullValue) {
            return jsonNodeFactory.nullNode();
        }
        if (input instanceof FloatValue) {
            return jsonNodeFactory.numberNode(((FloatValue) input).getValue());
        }
        if (input instanceof StringValue) {
            return jsonNodeFactory.textNode(((StringValue) input).getValue());
        }
        if (input instanceof IntValue) {
            return jsonNodeFactory.numberNode(((IntValue) input).getValue());
        }
        if (input instanceof BooleanValue) {
            return jsonNodeFactory.booleanNode(((BooleanValue) input).isValue());
        }
        if (input instanceof EnumValue) {
            return jsonNodeFactory.textNode(((EnumValue) input).getName());
        }
        if (input instanceof VariableReference) {
            String varName = ((VariableReference) input).getName();
            return parseLiteral(variables.get(varName), variables);
        }
        if (input instanceof ArrayValue) {
            List<Value> values = ((ArrayValue) input).getValues();
            return jsonNodeFactory
                    .arrayNode()
                    .addAll(
                            values.stream()
                                    .map(v -> parseLiteral(v, variables))
                                    .collect(Collectors.toList()));
        }
        if (input instanceof ObjectValue) {
            List<ObjectField> values = ((ObjectValue) input).getObjectFields();
            final ObjectNode objectNode = jsonNodeFactory.objectNode();
            values.forEach(
                    fld -> {
                        objectNode.replace(fld.getName(), parseLiteral(fld.getValue(), variables));
                    });
            return objectNode;
        }
        return Coercing.super.parseLiteral(input, variables);
    }

    @Override
    public Value valueToLiteral(Object input) {
        if (input == null) {
            return NullValue.newNullValue().build();
        }
        if (input instanceof String) {
            return new StringValue((String) input);
        }
        if (input instanceof Float) {
            return new FloatValue(BigDecimal.valueOf((Float) input));
        }
        if (input instanceof Double) {
            return new FloatValue(BigDecimal.valueOf((Double) input));
        }
        if (input instanceof BigDecimal) {
            return new FloatValue((BigDecimal) input);
        }
        if (input instanceof BigInteger) {
            return new IntValue((BigInteger) input);
        }
        if (input instanceof Number) {
            long l = ((Number) input).longValue();
            return new IntValue(BigInteger.valueOf(l));
        }
        if (input instanceof Boolean) {
            return new BooleanValue((Boolean) input);
        }
        if (FpKit.isIterable(input)) {
            return handleIterable(FpKit.toIterable(input));
        }
        if (input instanceof Map) {
            return handleMap((Map<?, ?>) input);
        }
        throw new UnsupportedOperationException(
                "The ObjectScalar cant handle values of type : " + input.getClass());
    }

    private Value<?> handleMap(Map<?, ?> map) {
        ObjectValue.Builder builder = ObjectValue.newObjectValue();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String name = String.valueOf(entry.getKey());
            Value<?> value = valueToLiteral(entry.getValue());

            builder.objectField(newObjectField().name(name).value(value).build());
        }
        return builder.build();
    }

    @SuppressWarnings("rawtypes")
    private Value<?> handleIterable(Iterable<?> input) {
        List<Value> values = new ArrayList<>();
        for (Object val : input) {
            values.add(valueToLiteral(val));
        }
        return ArrayValue.newArrayValue().values(values).build();
    }
}
