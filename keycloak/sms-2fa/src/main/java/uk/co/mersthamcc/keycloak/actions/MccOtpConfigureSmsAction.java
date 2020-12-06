package uk.co.mersthamcc.keycloak.actions;

import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.UserModel;
import uk.co.mersthamcc.keycloak.helpers.MccOtpSmsHelper;
import uk.co.mersthamcc.keycloak.smsprovider.SmsProvider;
import uk.co.mersthamcc.keycloak.smsprovider.SmsProviderFactory;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static uk.co.mersthamcc.keycloak.authenticator.KeycloakConfigurableTwoFactorAuthenticator.MOBILE_PHONE_ATTR;

public class MccOtpConfigureSmsAction implements RequiredActionProvider {

    public static final String PROVIDER_ID = "mcc-configure-otp-sms";

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        // Not used
    }

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
        SmsProvider provider = getSmsProvider();
        MultivaluedMap<String, String> form = context.getHttpRequest().getDecodedFormParameters();
        UserModel user = context.getUser();
        if (form.containsKey("otp")) {
            if (provider.validate(context.getAuthenticationSession(), form.getFirst("otp"))) {
                context.success();
            } else {
                context.failure();
            }
        } else if (form.containsKey("mobile_number")){
            if (MccOtpSmsHelper.processUpdate(context.getUser(), form)) {
                provider.send(context.getAuthenticationSession(), user.getFirstAttribute(MOBILE_PHONE_ATTR));
                context.challenge(context.form().createLoginTotp());
            } else {
                Response challenge = context.form()
                        .setError("mobile_number.no.valid")
                        .createForm("configure-sms.ftl");
                context.challenge(challenge);
            }
        } else {
            context.failure();
        }
    }

    @Override
    public void close() {
        // Not used
    }

    public SmsProvider getSmsProvider() {
        return SmsProviderFactory.create();
    }
}
