package uk.co.mersthamcc.keycloak.actions;

import org.jboss.logging.Logger;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.UserModel;
import uk.co.mersthamcc.keycloak.helpers.MccOtpSmsHelper;
import uk.co.mersthamcc.keycloak.smsprovider.SmsProvider;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static uk.co.mersthamcc.keycloak.authenticator.KeycloakConfigurableTwoFactorAuthenticator.MOBILE_PHONE_ATTR;

public class MccOtpConfigureSmsAction implements RequiredActionProvider {

    private static Logger logger = Logger.getLogger(MccOtpConfigureSmsAction.class);
    public static final String PROVIDER_ID = "mcc-configure-otp-sms";

    private SmsProvider smsProvider;

    @Override
    public void evaluateTriggers(RequiredActionContext context) {}

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        UserModel user = context.getUser();
        String mobileNumber = user.getFirstAttribute(MOBILE_PHONE_ATTR);

        Response challenge = context.form()
                .setAttribute("phoneNumber", mobileNumber)
                .createForm("configure-sms.ftl");
        context.challenge(challenge);
    }

    @Override
    public void processAction(RequiredActionContext context) {
        if (MccOtpSmsHelper.processUpdate(context.getUser(), context.getHttpRequest().getDecodedFormParameters())) {
            context.success();
        } else {
            Response challenge = context.form()
                    .setError("mobile_number.no.valid")
                    .createForm("sms-validation-mobile-number.ftl");
            context.challenge(challenge);
        }
    }

    @Override
    public void close() {}
}
