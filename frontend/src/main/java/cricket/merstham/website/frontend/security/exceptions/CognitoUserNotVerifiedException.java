package cricket.merstham.website.frontend.security.exceptions;

import org.springframework.security.core.AuthenticationException;

public class CognitoUserNotVerifiedException extends AuthenticationException {
    public CognitoUserNotVerifiedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CognitoUserNotVerifiedException(String msg) {
        super(msg);
    }
}
