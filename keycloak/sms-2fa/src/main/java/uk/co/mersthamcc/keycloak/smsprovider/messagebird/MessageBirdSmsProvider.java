package uk.co.mersthamcc.keycloak.smsprovider.messagebird;

import com.messagebird.MessageBirdClient;
import com.messagebird.MessageBirdServiceImpl;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.NotFoundException;
import com.messagebird.exceptions.UnauthorizedException;
import uk.co.mersthamcc.keycloak.smsprovider.SmsProvider;

public class MessageBirdSmsProvider implements SmsProvider {

    private final MessageBirdClient client;

    public MessageBirdSmsProvider() {
        client = new MessageBirdClient(new MessageBirdServiceImpl(System.getenv("MESSAGEBIRD_API_TOKEN")));
    }

    @Override
    public String send(String phoneNumber) {
        try {
            return client.sendVerifyToken(phoneNumber).getId();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
            throw new RuntimeException("UnauthorizedException encountered while sending SMS code via MessageBird", e);
        } catch (GeneralException e) {
            e.printStackTrace();
            throw new RuntimeException("Error sending SMS code via MessageBird", e);
        }
    }

    @Override
    public boolean validate(String validationId, String code) {
        try {
            return client.verifyToken(validationId, code).getStatus().equals("verified");
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
