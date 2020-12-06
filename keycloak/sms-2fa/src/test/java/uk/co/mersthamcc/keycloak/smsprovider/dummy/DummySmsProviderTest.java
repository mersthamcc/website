package uk.co.mersthamcc.keycloak.smsprovider.dummy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class DummySmsProviderTest {

    DummySmsProvider provider = new DummySmsProvider();

    @Mock
    AuthenticationSessionModel session;

    @Test
    void getName() {
        assertThat(provider.getName(), equalTo(DummySmsProvider.PROVIDER_NAME));
    }

    @Test
    void validateCorrectCode() {
        assertThat(provider.validate(session, "123456"), is(true));
    }

    @Test
    void validateIncorrectCode() {
        assertThat(provider.validate(session, "456789"), is(false));
    }
}