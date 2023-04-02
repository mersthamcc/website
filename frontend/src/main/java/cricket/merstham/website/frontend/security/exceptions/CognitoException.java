package cricket.merstham.website.frontend.security.exceptions;

public class CognitoException extends RuntimeException {
    public CognitoException(String message) {
        super(message);
    }

    public CognitoException(String message, Throwable cause) {
        super(message, cause);
    }
}
