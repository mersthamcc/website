package cricket.merstham.website.frontend.mappers;

import com.apollographql.apollo.api.CustomTypeAdapter;
import com.apollographql.apollo.api.CustomTypeValue;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.text.MessageFormat.format;

public class LocalDateCustomTypeAdapter implements CustomTypeAdapter<LocalDate> {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate decode(@NotNull CustomTypeValue<?> value) {
        try {
            return LocalDate.parse(value.value.toString(), formatter);
        } catch (Exception e) {
            throw new RuntimeException(format("Cannot parse date: {0}", value.value.toString()), e);
        }
    }

    @NotNull
    @Override
    public CustomTypeValue<?> encode(LocalDate value) {
        return new CustomTypeValue.GraphQLString(value.format(formatter));
    }
}
