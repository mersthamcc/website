package cricket.merstham.website.frontend.mappers;

import com.apollographql.apollo.api.ScalarType;
import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.time.Instant;

public enum CustomGraphQLScalars implements ScalarType {
    DATETIME,
    DATE,
    JSON,
    BYTES;

    @NotNull
    @Override
    public String className() {
        switch (this) {
            case DATE:
            case DATETIME:
                return Instant.class.getCanonicalName();
            case JSON:
                return JsonNode.class.getCanonicalName();
            case BYTES:
                return ByteBuffer.class.getCanonicalName();
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
            case JSON:
                return "Json";
            case BYTES:
                return "Bytes";
            default:
                throw new IllegalArgumentException();
        }
    }
}
