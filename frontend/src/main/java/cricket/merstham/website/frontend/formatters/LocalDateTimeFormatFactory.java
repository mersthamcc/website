package cricket.merstham.website.frontend.formatters;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.time.LocalDateTime;
import java.util.Set;

public class LocalDateTimeFormatFactory implements AnnotationFormatterFactory<LocalDateTimeFormat> {
    @Override
    public Set<Class<?>> getFieldTypes() {
        return Set.of(LocalDateTime.class);
    }

    @Override
    public Printer<?> getPrinter(LocalDateTimeFormat annotation, Class<?> fieldType) {
        return new LocalDateTimeFormatter(annotation.pattern(), annotation.resolverStyle());
    }

    @Override
    public Parser<?> getParser(LocalDateTimeFormat annotation, Class<?> fieldType) {
        return new LocalDateTimeFormatter(annotation.pattern(), annotation.resolverStyle());
    }
}
