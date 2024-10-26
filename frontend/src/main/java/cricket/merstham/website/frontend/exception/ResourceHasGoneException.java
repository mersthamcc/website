package cricket.merstham.website.frontend.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

import static org.springframework.http.HttpStatus.GONE;

@ResponseStatus(value = GONE)
public class ResourceHasGoneException extends RuntimeException {
    @Serial private static final long serialVersionUID = 7781819397758124998L;

    public ResourceHasGoneException(Throwable cause) {
        super("Resource has gone", cause);
    }

    public ResourceHasGoneException(String message) {
        super(message);
    }

    public ResourceHasGoneException() {
        super("Resource has gone");
    }
}
