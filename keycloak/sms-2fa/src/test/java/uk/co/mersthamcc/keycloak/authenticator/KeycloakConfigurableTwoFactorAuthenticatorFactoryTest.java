package uk.co.mersthamcc.keycloak.authenticator;

import org.junit.jupiter.api.Test;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.keycloak.models.AuthenticationExecutionModel.Requirement.*;
import static org.keycloak.provider.ProviderConfigProperty.ROLE_TYPE;
import static org.mockito.Mockito.mock;

class KeycloakConfigurableTwoFactorAuthenticatorFactoryTest {

    private final KeycloakConfigurableTwoFactorAuthenticatorFactory factory = new KeycloakConfigurableTwoFactorAuthenticatorFactory();

    @Test
    void getDisplayTypeReturnsExpectedValue() {
        assertThat(factory.getDisplayType(), is("MCC 2FA Authentication with SMS"));
    }

    @Test
    void getReferenceCategoryReturnsExpectedValue() {
        assertThat(factory.getReferenceCategory(), is("mcc-2fa"));
    }

    @Test
    void isConfigurableReturnsTrue() {
        assertThat(factory.isConfigurable(), is(true));
    }

    @Test
    void getRequirementChoicesReturnsExpectedValue() {
        AuthenticationExecutionModel.Requirement[] requirements = factory.getRequirementChoices();

        assertThat(requirements.length, equalTo(3));
        assertThat(requirements, hasItemInArray(REQUIRED));
        assertThat(requirements, hasItemInArray(ALTERNATIVE));
        assertThat(requirements, hasItemInArray(DISABLED));
    }

    @Test
    void isUserSetupAllowedReturnsTrue() {
        assertThat(factory.isUserSetupAllowed(), is(true));
    }

    @Test
    void getHelpTextReturnsExpectedValue() {
        assertThat(factory.getHelpText(), is("Validates an OTP sent by SMS or an authenticator app"));
    }

    @Test
    void getConfigPropertiesReturnsExpectedValue() {
        List<ProviderConfigProperty> config = factory.getConfigProperties();

        assertThat(config.size(), equalTo(1));
        assertThat(config.get(0).getName(), equalTo(KeycloakConfigurableTwoFactorAuthenticatorFactory.CONFIG_PROPERTY_FORCE_OTP_ROLE));
        assertThat(config.get(0).getType(), equalTo(ROLE_TYPE));
    }

    @Test
    void create() {
        Authenticator authenticator = factory.create(mock(KeycloakSession.class));

        assertThat(authenticator, instanceOf(KeycloakConfigurableTwoFactorAuthenticator.class));
    }

    @Test
    void createsSingleton() {
        Authenticator authenticator = factory.create(mock(KeycloakSession.class));
        assertThat(authenticator, instanceOf(KeycloakConfigurableTwoFactorAuthenticator.class));
        assertThat(factory.create(mock(KeycloakSession.class)), sameInstance(authenticator));
    }
}