package cricket.merstham.website.frontend.mappers;

import com.apollographql.apollo.api.CustomTypeAdapter;
import com.apollographql.apollo.api.CustomTypeValue;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import static java.text.MessageFormat.format;

public class LocalDateTimeCustomTypeAdapter implements CustomTypeAdapter<LocalDateTime> {

    private DateTimeFormatter formatter =
            new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                    .appendFraction(ChronoField.MICRO_OF_SECOND, 0, 6, true)
                    .appendZoneOrOffsetId()
                    .toFormatter();
    // DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");

    @Override
    public LocalDateTime decode(@NotNull CustomTypeValue<?> value) {
        try {
            return LocalDateTime.parse(value.value.toString(), formatter);
        } catch (Exception e) {
            throw new RuntimeException(
                    format("Cannot parse datetime: {0}", value.value.toString()), e);
        }
    }

    @NotNull
    @Override
    public CustomTypeValue<?> encode(LocalDateTime value) {
        return new CustomTypeValue.GraphQLString(value.atZone(ZoneId.of("UTC")).format(formatter));
    }
}
