package uk.co.mersthamcc.keycloak.smsprovider.messagebird;

import com.messagebird.MessageBirdClient;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.NotFoundException;
import com.messagebird.exceptions.UnauthorizedException;
import com.messagebird.objects.Verify;
import com.messagebird.objects.VerifyRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.mersthamcc.keycloak.TestHelpers.MOBILE_NUMBER;
import static uk.co.mersthamcc.keycloak.smsprovider.messagebird.MessageBirdSmsProvider.MESSAGEBIRD_VERIFY_TOKEN_AUTH_NOTE;

@ExtendWith(MockitoExtension.class)
class MessageBirdSmsProviderTest {

    private static final String VERIFY_TOKEN_VALUE = "abcd1234";

    private static final Verify VERIFY_RESPONSE = new Verify() {
        @Override
        public String getId() {
            return VERIFY_TOKEN_VALUE;
        }
    };

    @Mock
    MessageBirdClient mockClient;

    @Mock
    AuthenticationSessionModel session;

    MessageBirdSmsProvider provider = new TestMessageBirdSmsProvider();

    @Test
    void getName() {
        assertThat(provider.getName(), equalTo(MessageBirdSmsProvider.PROVIDER_NAME));
    }

    @Test
    void sendStoresTokenAsSessionNote() throws UnauthorizedException, GeneralException {
        VerifyRequest request = new VerifyRequest(MOBILE_NUMBER);
        when(mockClient.sendVerifyToken(argThat(matchesRequest(request)))).thenReturn(VERIFY_RESPONSE);
        provider.send(session, MOBILE_NUMBER);
        verify(session).setUserSessionNote(eq(MESSAGEBIRD_VERIFY_TOKEN_AUTH_NOTE), eq(VERIFY_TOKEN_VALUE));
    }

    @Test
    void validateMatchesCorrectCode() throws UnauthorizedException, GeneralException, NotFoundException {
        Verify response = new Verify();
        response.setId(VERIFY_TOKEN_VALUE);
        response.setStatus("verified");
        response.setRecipient(MOBILE_NUMBER);
        when(session.getUserSessionNotes()).thenReturn(Map.of(MESSAGEBIRD_VERIFY_TOKEN_AUTH_NOTE, VERIFY_TOKEN_VALUE));
        when(mockClient.verifyToken(eq(VERIFY_TOKEN_VALUE), eq("123456"))).thenReturn(response);

        assertThat(provider.validate(session, "123456"), is(true));
    }

    @Test
    void validateFailsWithIncorrectCode() throws UnauthorizedException, GeneralException, NotFoundException {
        Verify response = new Verify();
        response.setId(VERIFY_TOKEN_VALUE);
        response.setStatus("failed");
        response.setRecipient(MOBILE_NUMBER);
        when(session.getUserSessionNotes()).thenReturn(Map.of(MESSAGEBIRD_VERIFY_TOKEN_AUTH_NOTE, VERIFY_TOKEN_VALUE));
        when(mockClient.verifyToken(eq(VERIFY_TOKEN_VALUE), eq("123456"))).thenReturn(response);

        assertThat(provider.validate(session, "123456"), is(false));
    }

    public static VerifyRequestMatcher matchesRequest(VerifyRequest request) {
        return new VerifyRequestMatcher(request);
    }

    private class TestMessageBirdSmsProvider extends MessageBirdSmsProvider {
        @Override
        protected MessageBirdClient getClient() {
            return mockClient;
        }
    }

    private static class VerifyRequestMatcher implements ArgumentMatcher<VerifyRequest> {

        private final VerifyRequest request;

        public VerifyRequestMatcher(VerifyRequest request) {
            this.request = request;
        }

        @Override
        public boolean matches(VerifyRequest argument) {
            return request.getRecipient().equals(argument.getRecipient());
        }

    }
}