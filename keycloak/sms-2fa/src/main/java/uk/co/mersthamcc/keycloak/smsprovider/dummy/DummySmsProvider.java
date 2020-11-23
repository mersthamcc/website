package uk.co.mersthamcc.keycloak.smsprovider.dummy;

import org.keycloak.sessions.AuthenticationSessionModel;
import uk.co.mersthamcc.keycloak.smsprovider.SmsProvider;

public class DummySmsProvider implements SmsProvider {

    public static final String PROVIDER_NAME = "DUMMY";

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }

    @Override
    public void send(AuthenticationSessionModel session, String phoneNumber) {
        // Pretend to do something
    }

    @Override
    public boolean validate(AuthenticationSessionModel session, String code) {
        return code.equals("123456");
    }
}
