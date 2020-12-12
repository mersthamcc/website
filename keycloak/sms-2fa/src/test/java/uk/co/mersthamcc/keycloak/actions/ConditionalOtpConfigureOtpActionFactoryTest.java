package uk.co.mersthamcc.keycloak.actions;

import org.junit.jupiter.api.Test;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.mock;

class ConditionalOtpConfigureOtpActionFactoryTest {

    private final ConditionalOtpConfigureOtpActionFactory factory = new ConditionalOtpConfigureOtpActionFactory();

    @Test
    void create() {
        RequiredActionProvider action = factory.create(mock(KeycloakSession.class));

        assertThat(action, instanceOf(ConditionalOtpConfigureOtpAction.class));
    }

    @Test
    void getId() {
        assertThat(factory.getId(), equalTo(ConditionalOtpConfigureOtpAction.PROVIDER_ID));
    }

    @Test
    void getDisplayText() {
        assertThat(factory.getDisplayText(), equalTo("Configure SMS for OTP"));
    }
}