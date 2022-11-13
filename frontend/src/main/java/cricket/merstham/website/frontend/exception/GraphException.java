package cricket.merstham.website.frontend.exception;

import com.apollographql.apollo.api.Error;
import lombok.Getter;

import java.io.Serial;
import java.util.List;

@Getter
public class GraphException extends RuntimeException {

    @Serial private static final long serialVersionUID = 1580147958932642509L;

    private final List<Error> errors;

    public GraphException(String message, List<Error> errors) {
        super(message);
        this.errors = errors;
    }
}
