package uk.co.mersthamcc.keycloak.helpers;

import org.jboss.logging.Logger;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;

import static uk.co.mersthamcc.keycloak.ConditionalOtpConstants.MOBILE_PHONE_ATTR;

public class ConditionalOtpSmsHelper {

    private static Logger logger = Logger.getLogger(ConditionalOtpSmsHelper.class);

    private ConditionalOtpSmsHelper() {
        // Not used
    }

    public static boolean processUpdate(UserModel user, MultivaluedMap<String, String> form) {
        String answer = (form.getFirst("mobile_number"));
        if (answer != null && answer.trim().length() > 0) {
            logger.debug("Valid matching mobile numbers supplied, save credential ...");

            user.setAttribute(MOBILE_PHONE_ATTR, List.of(normalisePhoneNumber(answer)));
            return true;

        } else {
            logger.debug("The field wasn't complete or is an invalid number...");
            return false;
        }
    }

    public static String normalisePhoneNumber(String inputNumber) {
        inputNumber = inputNumber.trim().replaceAll("[^+\\d]|(?<=.)\\+", "");
        if (inputNumber.startsWith("00")) {
            inputNumber = inputNumber.replaceFirst("00", "+");
        }
        if (inputNumber.startsWith("0")) {
            inputNumber = inputNumber.replaceFirst("0", "+44");
        }
        if (inputNumber.startsWith("44")) {
            inputNumber = inputNumber.replaceFirst("44", "+44");
        }
        return inputNumber;
    }
}
