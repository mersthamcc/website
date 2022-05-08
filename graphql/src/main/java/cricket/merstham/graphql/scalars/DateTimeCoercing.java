package cricket.merstham.graphql.scalars;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

import java.time.Instant;
import java.time.format.DateTimeParseException;

public class DateTimeCoercing implements Coercing<Instant, String> {
    @Override
    public String serialize(final Object dataFetcherResult) throws CoercingSerializeException {
        if (dataFetcherResult instanceof Instant) {
            return dataFetcherResult.toString();
        } else {
            throw new CoercingSerializeException("Expected a Instant object.");
        }
    }

    @Override
    public Instant parseValue(final Object input) throws CoercingParseValueException {
        try {
            if (input instanceof String) {
                return Instant.parse((String) input);
            } else {
                throw new CoercingParseValueException("Expected a String");
            }
        } catch (DateTimeParseException e) {
            throw new CoercingParseValueException(
                    String.format("Not a valid date: '%s'.", input), e);
        }
    }

    @Override
    public Instant parseLiteral(final Object input) throws CoercingParseLiteralException {
        if (input instanceof StringValue) {
            try {
                return Instant.parse(((StringValue) input).getValue());
            } catch (DateTimeParseException e) {
                throw new CoercingParseLiteralException(e);
            }
        } else {
            throw new CoercingParseLiteralException("Expected a StringValue.");
        }
    }
}
