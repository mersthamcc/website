package cricket.merstham.website.frontend.formatters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.format.ResolverStyle;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InstantFormat {
    String pattern() default "yyyy-MM-ddTHH:mm:ssZ";

    ResolverStyle resolverStyle() default ResolverStyle.LENIENT;
}
