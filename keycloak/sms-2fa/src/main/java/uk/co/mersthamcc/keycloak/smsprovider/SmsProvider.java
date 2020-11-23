package uk.co.mersthamcc.keycloak.smsprovider;

import org.keycloak.sessions.AuthenticationSessionModel;

import java.util.ServiceLoader;

public interface SmsProvider {
    void send(AuthenticationSessionModel session, String phoneNumber);
    boolean validate(AuthenticationSessionModel session, String code);
}
