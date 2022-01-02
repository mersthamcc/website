package cricket.merstham.website.frontend.formatters;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

import static java.text.MessageFormat.format;

public class LocalDateTimeFormatter implements Formatter<LocalDateTime> {

    private final DateTimeFormatter formatter;

    public LocalDateTimeFormatter(String pattern, ResolverStyle resolverStyle) {
        this.formatter = DateTimeFormatter.ofPattern(pattern).withResolverStyle(resolverStyle);
    }

    @Override
    public LocalDateTime parse(String text, Locale locale) throws ParseException {
        try {
            return LocalDateTime.parse(text, formatter.withLocale(locale));
        } catch (Exception e) {
            throw new RuntimeException(format("Cannot parse datetime: {0}", text), e);
        }
    }

    @Override
    public String print(LocalDateTime object, Locale locale) {
        return object.format(formatter.withLocale(locale));
    }
}
