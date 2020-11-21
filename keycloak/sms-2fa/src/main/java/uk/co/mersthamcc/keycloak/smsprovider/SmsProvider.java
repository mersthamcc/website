package uk.co.mersthamcc.keycloak.smsprovider;

import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.NotFoundException;
import com.messagebird.exceptions.UnauthorizedException;

public interface SmsProvider {
    String send(String phoneNumber);
    boolean validate(String validationId, String code);
}
