package cricket.merstham.website.frontend.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class EntitySaveException extends RuntimeException {
    private final List<String> errors;

    public EntitySaveException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public EntitySaveException(String message, Throwable cause, List<String> errors) {
        super(message, cause);
        this.errors = errors;
    }
}
