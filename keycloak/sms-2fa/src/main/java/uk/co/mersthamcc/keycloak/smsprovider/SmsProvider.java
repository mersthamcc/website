package uk.co.mersthamcc.keycloak.smsprovider;

import java.util.ServiceLoader;

public interface SmsProvider {
    String send(String phoneNumber);
    boolean validate(String validationId, String code);
}
