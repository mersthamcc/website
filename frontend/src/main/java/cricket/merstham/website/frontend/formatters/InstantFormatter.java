package cricket.merstham.website.frontend.formatters;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import static java.text.MessageFormat.format;

public class InstantFormatter implements Formatter<Instant> {

    private final DateTimeFormatter formatter;

    public InstantFormatter(String pattern, ResolverStyle resolverStyle) {
        this.formatter = DateTimeFormatter.ofPattern(pattern).withResolverStyle(resolverStyle);
    }

    @Override
    public Instant parse(String text, Locale locale) throws ParseException {
        try {
            return Instant.parse(text);
        } catch (DateTimeParseException e) {
            throw new RuntimeException(format("Cannot parse datetime: {0}", text), e);
        }
    }

    @Override
    public String print(Instant object, Locale locale) {
        return object.atZone(ZoneId.systemDefault()).format(formatter.withLocale(locale));
    }
}
