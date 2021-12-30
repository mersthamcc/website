package cricket.merstham.website.frontend.formatters;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

import static java.text.MessageFormat.format;

public class LocalDateTimeDateOnlyFormatter implements Formatter<LocalDateTime> {

    private final DateTimeFormatter formatter;
    private final String time;

    public LocalDateTimeDateOnlyFormatter(String pattern, ResolverStyle resolverStyle, String time) {
        this.formatter = DateTimeFormatter.ofPattern(pattern).withResolverStyle(resolverStyle);
        this.time = time;
    }

    @Override
    public LocalDateTime parse(String text, Locale locale) throws ParseException {
        try {
            var date = LocalDate.parse(text, formatter.withLocale(locale));
            return date.atStartOfDay();
        } catch (Exception e) {
            throw new RuntimeException(
                    format("Cannot parse datetime: {0}", text), e);
        }
    }

    @Override
    public String print(LocalDateTime object, Locale locale) {
        return object.format(formatter.withLocale(locale));
    }
}
