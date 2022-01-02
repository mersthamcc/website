package cricket.merstham.website.frontend.formatters;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.time.LocalDateTime;
import java.util.Set;

public class LocalDateTimeDateOnlyFormatFactory
        implements AnnotationFormatterFactory<LocalDateTimeDateOnlyFormat> {
    @Override
    public Set<Class<?>> getFieldTypes() {
        return Set.of(LocalDateTime.class);
    }

    @Override
    public Printer<?> getPrinter(LocalDateTimeDateOnlyFormat annotation, Class<?> fieldType) {
        return new LocalDateTimeDateOnlyFormatter(
                annotation.pattern(), annotation.resolverStyle(), annotation.time());
    }

    @Override
    public Parser<?> getParser(LocalDateTimeDateOnlyFormat annotation, Class<?> fieldType) {
        return new LocalDateTimeDateOnlyFormatter(
                annotation.pattern(), annotation.resolverStyle(), annotation.time());
    }
}
