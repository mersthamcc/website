package uk.co.mersthamcc.keycloak.helpers;

import org.jboss.logging.Logger;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;

import static uk.co.mersthamcc.keycloak.authenticator.KeycloakConfigurableTwoFactorAuthenticator.MOBILE_PHONE_ATTR;

public class MccOtpSmsHelper {

    private static Logger logger = Logger.getLogger(MccOtpSmsHelper.class);

    public static boolean processUpdate(UserModel user, MultivaluedMap<String, String> form) {
        String answer = (form.getFirst("mobile_number"));
        if (answer != null && answer.length() > 0) {
            logger.debug("Valid matching mobile numbers supplied, save credential ...");
            List<String> mobileNumber = new ArrayList<>();
            mobileNumber.add(answer);

            user.setAttribute(MOBILE_PHONE_ATTR, mobileNumber);
            return true;

        } else {
            logger.debug("The field wasn't complete or is an invalid number...");
            return false;
        }
    }
}
