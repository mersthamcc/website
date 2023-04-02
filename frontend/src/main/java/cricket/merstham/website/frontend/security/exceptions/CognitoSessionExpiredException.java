package cricket.merstham.website.frontend.security.exceptions;

public class CognitoSessionExpiredException extends CognitoException {
    public CognitoSessionExpiredException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
