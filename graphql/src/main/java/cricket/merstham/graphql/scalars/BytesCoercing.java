package cricket.merstham.graphql.scalars;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class BytesCoercing implements Coercing<InputStream, String> {
    @Override
    public String serialize(final Object dataFetcherResult) throws CoercingSerializeException {
        if (dataFetcherResult instanceof InputStream inputStream) {
            try {
                return Base64.getEncoder().encodeToString(inputStream.readAllBytes());
            } catch (IOException e) {
                throw new CoercingSerializeException("Error encoding input stream", e);
            }
        } else {
            throw new CoercingSerializeException("Expected an InputStream object.");
        }
    }

    @Override
    public InputStream parseValue(final Object input) throws CoercingParseValueException {
        if (input instanceof String encoded) {
            return new ByteArrayInputStream(Base64.getDecoder().decode(encoded));
        } else {
            throw new CoercingParseValueException("Expected a String");
        }
    }

    @Override
    public InputStream parseLiteral(final Object input) throws CoercingParseLiteralException {
        if (input instanceof StringValue encoded) {
            return new ByteArrayInputStream(Base64.getDecoder().decode(encoded.getValue()));
        } else {
            throw new CoercingParseLiteralException("Expected a StringValue.");
        }
    }
}
