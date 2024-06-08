package cricket.merstham.website.frontend.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(value = NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    @Serial private static final long serialVersionUID = -5612589057382146790L;

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException() {
        super("Resource not found");
    }
}
