package uk.co.mersthamcc.keycloak.smsprovider.messagebird;

import com.messagebird.MessageBirdClient;
import com.messagebird.MessageBirdServiceImpl;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.NotFoundException;
import com.messagebird.exceptions.UnauthorizedException;
import com.messagebird.objects.VerifyRequest;
import org.keycloak.sessions.AuthenticationSessionModel;
import uk.co.mersthamcc.keycloak.smsprovider.SmsProvider;

public class MessageBirdSmsProvider implements SmsProvider {

    private final static String MESSAGEBIRD_VERIFY_TOKEN_AUTH_NOTE = "MESSAGEBIRD_VERIFY_TOKEN";
    private final MessageBirdClient client;
    private final String originator;

    public MessageBirdSmsProvider() {
        client = new MessageBirdClient(new MessageBirdServiceImpl(System.getenv("MESSAGEBIRD_API_TOKEN")));
        originator = System.getenv().getOrDefault("SMS_OTP_ORIGINATOR", null);
    }

    @Override
    public void send(AuthenticationSessionModel session, String phoneNumber) {
        try {
            VerifyRequest request = new VerifyRequest(phoneNumber);
            request.setOriginator(originator);
            String id = client.sendVerifyToken(request).getId();
            session.setUserSessionNote(MESSAGEBIRD_VERIFY_TOKEN_AUTH_NOTE, id);
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            throw new RuntimeException("UnauthorizedException encountered while sending SMS code via MessageBird", e);
        } catch (GeneralException e) {
            e.printStackTrace();
            throw new RuntimeException("Error sending SMS code via MessageBird", e);
        }
    }

    @Override
    public boolean validate(AuthenticationSessionModel session, String code) {
        try {
            String id = session.getUserSessionNotes().get(MESSAGEBIRD_VERIFY_TOKEN_AUTH_NOTE);
            return client.verifyToken(id, code).getStatus().equals("verified");
        } catch (NotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("NotFoundException encountered while validating code via MessageBird", e);
        } catch (GeneralException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while validating code via MessageBird", e);
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            throw new RuntimeException("UnauthorizedException encountered while validating code via MessageBird", e);
        }
    }
}
