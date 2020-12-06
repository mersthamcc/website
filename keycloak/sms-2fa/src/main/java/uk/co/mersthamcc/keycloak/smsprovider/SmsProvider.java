package uk.co.mersthamcc.keycloak.smsprovider;

import org.keycloak.sessions.AuthenticationSessionModel;


public interface SmsProvider {
    String getName();
    void send(AuthenticationSessionModel session, String phoneNumber);
    boolean validate(AuthenticationSessionModel session, String code);
}
