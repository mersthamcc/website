package uk.co.mersthamcc.keycloak.smsprovider.messagebird;

import com.messagebird.MessageBirdClient;
import com.messagebird.MessageBirdServiceImpl;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.NotFoundException;
import com.messagebird.exceptions.UnauthorizedException;
import com.messagebird.objects.VerifyRequest;
import org.keycloak.sessions.AuthenticationSessionModel;
import uk.co.mersthamcc.keycloak.smsprovider.SmsProvider;
import uk.co.mersthamcc.keycloak.smsprovider.SmsProviderException;

public class MessageBirdSmsProvider implements SmsProvider {

    public static final String MESSAGEBIRD_VERIFY_TOKEN_AUTH_NOTE = "MESSAGEBIRD_VERIFY_TOKEN";
    public static final String PROVIDER_NAME = "MESSAGEBIRD";
    public static final String API_TOKEN_ENVIRONMENT_VARIABLE = "MESSAGEBIRD_API_TOKEN";
    public static final String ORIGINATOR_ENVIRONMENT_VARIABLE = "SMS_OTP_ORIGINATOR";
    protected MessageBirdClient client;
    private final String originator;

    public MessageBirdSmsProvider() {
        this.originator = System.getenv().getOrDefault(ORIGINATOR_ENVIRONMENT_VARIABLE, null);
    }

    @Override
    public String getName() {
        return PROVIDER_NAME;
    }

    @Override
    public void send(AuthenticationSessionModel session, String phoneNumber) {
        try {
            VerifyRequest request = new VerifyRequest(phoneNumber);
            if (originator != null ) request.setOriginator(originator);
            String id = getClient().sendVerifyToken(request).getId();
            session.setUserSessionNote(MESSAGEBIRD_VERIFY_TOKEN_AUTH_NOTE, id);
        } catch (UnauthorizedException e) {
            throw new SmsProviderException("UnauthorizedException encountered while sending SMS code via MessageBird", e);
        } catch (GeneralException e) {
            throw new SmsProviderException("Error sending SMS code via MessageBird", e);
        }
    }

    @Override
    public boolean validate(AuthenticationSessionModel session, String code) {
        try {
            String id = session.getUserSessionNotes().get(MESSAGEBIRD_VERIFY_TOKEN_AUTH_NOTE);
            return getClient().verifyToken(id, code).getStatus().equals("verified");
        } catch (NotFoundException e) {
            throw new SmsProviderException("NotFoundException encountered while validating code via MessageBird", e);
        } catch (GeneralException e) {
            throw new SmsProviderException("Error while validating code via MessageBird", e);
        } catch (UnauthorizedException e) {
            throw new SmsProviderException("UnauthorizedException encountered while validating code via MessageBird", e);
        }
    }

    protected MessageBirdClient getClient() {
        if (client == null) {
            client = new MessageBirdClient(new MessageBirdServiceImpl(System.getenv(API_TOKEN_ENVIRONMENT_VARIABLE)));
        }
        return client;
    }
}
