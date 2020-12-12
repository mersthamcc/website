package uk.co.mersthamcc.keycloak.actions;

import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class ConditionalOtpConfigureOtpActionFactory implements RequiredActionFactory {

    private static final ConditionalOtpConfigureOtpAction SINGLETON = new ConditionalOtpConfigureOtpAction();

    public RequiredActionProvider create(KeycloakSession session) {
        return SINGLETON;
    }

    public String getId() {
        return ConditionalOtpConfigureOtpAction.PROVIDER_ID;
    }

    public String getDisplayText() {
        return "Configure SMS for OTP";
    }

    public void init(Config.Scope config) {
        // Not used
    }

    public void postInit(KeycloakSessionFactory factory) {
        // Not used
    }

    public void close() {
        // Not used
    }
}