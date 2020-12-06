package uk.co.mersthamcc.keycloak.smsprovider;

public class SmsProviderException extends RuntimeException {
    public SmsProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
