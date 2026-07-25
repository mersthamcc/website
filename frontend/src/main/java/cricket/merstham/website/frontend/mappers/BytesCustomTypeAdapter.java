package cricket.merstham.website.frontend.mappers;

import com.apollographql.apollo.api.CustomTypeAdapter;
import com.apollographql.apollo.api.CustomTypeValue;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Base64;

import static java.text.MessageFormat.format;

public class BytesCustomTypeAdapter implements CustomTypeAdapter<ByteBuffer> {

    @Override
    public ByteBuffer decode(@NotNull CustomTypeValue<?> value) {
        try {
            return ByteBuffer.wrap(Base64.getDecoder().decode(value.value.toString()));
        } catch (Exception e) {
            throw new RuntimeException(format("Cannot parse base64: {0}", value.value), e);
        }
    }

    @NotNull
    @Override
    public CustomTypeValue<?> encode(ByteBuffer value) {
        return new CustomTypeValue.GraphQLString(Base64.getEncoder().encodeToString(value.array()));
    }
}
