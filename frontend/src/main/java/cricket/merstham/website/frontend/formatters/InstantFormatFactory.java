package cricket.merstham.website.frontend.formatters;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.time.Instant;
import java.util.Set;

public class InstantFormatFactory implements AnnotationFormatterFactory<InstantFormat> {
    @Override
    public Set<Class<?>> getFieldTypes() {
        return Set.of(Instant.class);
    }

    @Override
    public Printer<Instant> getPrinter(InstantFormat annotation, Class<?> fieldType) {
        return new InstantFormatter(annotation.pattern(), annotation.resolverStyle());
    }

    @Override
    public Parser<Instant> getParser(InstantFormat annotation, Class<?> fieldType) {
        return new InstantFormatter(annotation.pattern(), annotation.resolverStyle());
    }
}
