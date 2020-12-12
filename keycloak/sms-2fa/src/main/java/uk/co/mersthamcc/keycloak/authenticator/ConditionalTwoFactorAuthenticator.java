package uk.co.mersthamcc.keycloak.authenticator;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.*;
import uk.co.mersthamcc.keycloak.actions.ConditionalOtpConfigureOtpAction;
import uk.co.mersthamcc.keycloak.smsprovider.SmsProvider;
import uk.co.mersthamcc.keycloak.smsprovider.SmsProviderFactory;

import javax.ws.rs.core.MultivaluedMap;

import static org.keycloak.models.utils.KeycloakModelUtils.getRoleFromString;
import static uk.co.mersthamcc.keycloak.ConditionalOtpConstants.CONFIG_PROPERTY_FORCE_OTP_ROLE;
import static uk.co.mersthamcc.keycloak.ConditionalOtpConstants.MOBILE_PHONE_ATTR;

public class ConditionalTwoFactorAuthenticator implements Authenticator {

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();
        if (otpRequired(context)) {
            if (user.getAttributes().containsKey(MOBILE_PHONE_ATTR)) {
                SmsProvider provider = getSmsProvider();

                provider.send(context.getAuthenticationSession(), user.getFirstAttribute(MOBILE_PHONE_ATTR));
                context.challenge(context.form().createLoginTotp());
            } else {
                setRequiredActions(context.getSession(), context.getRealm(), user);
                context.success();
            }
        } else {
            context.success();
        }
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        SmsProvider provider = getSmsProvider();
        MultivaluedMap<String, String> form = context.getHttpRequest().getDecodedFormParameters();
        String otp = form.getFirst("otp");

        if (provider.validate(context.getAuthenticationSession(), otp)) {
            context.success();
        } else {
            context.failure(AuthenticationFlowError.EXPIRED_CODE);
        }
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        user.addRequiredAction(ConditionalOtpConfigureOtpAction.PROVIDER_ID);
    }

    @Override
    public void close() {
        // Not used
    }

    private SmsProvider getSmsProvider() {
        return SmsProviderFactory.create();
    }

    private boolean otpRequired(AuthenticationFlowContext context) {
        AuthenticatorConfigModel authenticatorConfig = context.getAuthenticatorConfig();
        UserModel user = context.getUser();

        return userHasRole(context.getRealm(), user, authenticatorConfig.getConfig().get(CONFIG_PROPERTY_FORCE_OTP_ROLE));
    }

    private boolean userHasRole(RealmModel realm, UserModel user, String roleName) {
        if (roleName == null) {
            return false;
        }

        RoleModel role = getRoleFromString(realm, roleName);
        if (role != null) {
            return user.hasRole(role);
        }
        return false;
    }
}
