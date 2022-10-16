package cricket.merstham.website.frontend.mappers;

import com.apollographql.apollo.api.CustomTypeAdapter;
import com.apollographql.apollo.api.CustomTypeValue;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

import static java.text.MessageFormat.format;

public class InstantCustomTypeAdapter implements CustomTypeAdapter<Instant> {

    @Override
    public Instant decode(@NotNull CustomTypeValue<?> value) {
        try {
            return Instant.parse(value.value.toString());
        } catch (Exception e) {
            throw new RuntimeException(format("Cannot parse date: {0}", value.value.toString()), e);
        }
    }

    @NotNull
    @Override
    public CustomTypeValue<?> encode(Instant value) {
        return new CustomTypeValue.GraphQLString(value.toString());
    }
}
