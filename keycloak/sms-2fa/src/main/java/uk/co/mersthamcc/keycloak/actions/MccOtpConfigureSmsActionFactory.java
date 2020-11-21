package uk.co.mersthamcc.keycloak.actions;

import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class MccOtpConfigureSmsActionFactory implements RequiredActionFactory {

    private static final MccOtpConfigureSmsAction SINGLETON = new MccOtpConfigureSmsAction();

    public RequiredActionProvider create(KeycloakSession session) {
        return SINGLETON;
    }

    public String getId() {
        return MccOtpConfigureSmsAction.PROVIDER_ID;
    }

    public String getDisplayText() {
        return "Configure SMS for OTP";
    }

    public void init(Config.Scope config) {

    }

    public void postInit(KeycloakSessionFactory factory) {
    }

    public void close() {

    }
}