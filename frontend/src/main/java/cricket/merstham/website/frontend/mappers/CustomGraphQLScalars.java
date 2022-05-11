package cricket.merstham.website.frontend.mappers;

import com.apollographql.apollo.api.ScalarType;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public enum CustomGraphQLScalars implements ScalarType {
    DATETIME,
    DATE;

    @NotNull
    @Override
    public String className() {
        switch (this) {
            case DATE:
                return LocalDate.class.getCanonicalName();
            case DATETIME:
                return LocalDateTime.class.getCanonicalName();
            default:
                throw new IllegalArgumentException();
        }
    }

    @NotNull
    @Override
    public String typeName() {
        switch (this) {
            case DATE:
                return "Date";
            case DATETIME:
                return "DateTime";
            default:
                throw new IllegalArgumentException();
        }
    }
}
