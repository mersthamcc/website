package uk.co.mersthamcc.keycloak.authenticator;

import org.jboss.resteasy.spi.HttpRequest;
import org.junit.jupiter.api.Test;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.MockedStatic;
import uk.co.mersthamcc.keycloak.actions.MccOtpConfigureSmsAction;
import uk.co.mersthamcc.keycloak.smsprovider.SmsProvider;
import uk.co.mersthamcc.keycloak.smsprovider.SmsProviderFactory;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static uk.co.mersthamcc.keycloak.TestHelpers.createSmsProviderFactoryStaticMock;
import static uk.co.mersthamcc.keycloak.authenticator.KeycloakConfigurableTwoFactorAuthenticatorFactory.CONFIG_PROPERTY_FORCE_OTP_ROLE;

class KeycloakConfigurableTwoFactorAuthenticatorTest {

    private static final String OTP_ROLE = "OTP_ROLE";

    private KeycloakConfigurableTwoFactorAuthenticator authenticator = new KeycloakConfigurableTwoFactorAuthenticator();

    AuthenticationFlowContext context;
    Response otpFormResponse;
    RoleModel role;

    private void setupAuthenticateMocks(boolean userHasRole, boolean userHasPhoneNumber) {
        AuthenticatorConfigModel config = new AuthenticatorConfigModel();
        config.setConfig(Map.of(
                CONFIG_PROPERTY_FORCE_OTP_ROLE, OTP_ROLE
        ));
        UserModel user = mock(UserModel.class);
        role = mock(RoleModel.class);
        when(role.getName()).thenReturn(OTP_ROLE);
        when(user.hasRole(eq(role))).thenReturn(userHasRole);

        if (userHasPhoneNumber) {
            when(user.getAttributes()).thenReturn(Map.of(
                    KeycloakConfigurableTwoFactorAuthenticator.MOBILE_PHONE_ATTR, List.of("+447777123456")
            ));
        }

        context = mock(AuthenticationFlowContext.class);
        LoginFormsProvider provider = mock(LoginFormsProvider.class);
        otpFormResponse = mock(Response.class);
        when(provider.createLoginTotp()).thenReturn(otpFormResponse);

        when(context.form()).thenReturn(provider);
        when(context.getAuthenticatorConfig()).thenReturn(config);
        when(context.getUser()).thenReturn(user);
    }

    private void setupActionMocks() {
        context = mock(AuthenticationFlowContext.class);
        HttpRequest request = mock(HttpRequest.class);
        MultivaluedHashMap<String, String> form = new MultivaluedHashMap<>();
        form.put("otp", List.of("123456"));
        when(request.getDecodedFormParameters()).thenReturn(form);
        when(context.getHttpRequest()).thenReturn(request);
        when(context.getAuthenticationSession()).thenReturn(mock(AuthenticationSessionModel.class));
    }

    private MockedStatic<KeycloakModelUtils> createKeycloakUtilsStaticMock() {
        MockedStatic<KeycloakModelUtils> utilsMockedStatic = mockStatic(KeycloakModelUtils.class);
        utilsMockedStatic.when( () -> { KeycloakModelUtils.getRoleFromString(any(), eq(OTP_ROLE)); }).thenReturn(role);
        return utilsMockedStatic;
    }

    @Test
    void authenticateDisplayOtpFormIfConfigured() {
        setupAuthenticateMocks(true, true);
        try(MockedStatic<KeycloakModelUtils> modelUtilsMockedStatic = createKeycloakUtilsStaticMock()) {
            authenticator.authenticate(context);
            verify(context).challenge(eq(otpFormResponse));
        }
    }

    @Test
    void authenticateSucceedsIfNotInRole() {
        setupAuthenticateMocks(false, false);
        try(MockedStatic<KeycloakModelUtils> modelUtilsMockedStatic = createKeycloakUtilsStaticMock()) {
            authenticator.authenticate(context);
            verify(context).success();
            verify(context.getUser(), times(0)).addRequiredAction(any(String.class));
        }
    }

    @Test
    void authenticateSucceedsButAddsRequiredActionIfUserHasRoleButNoPhoneNumber() {
        setupAuthenticateMocks(true, false);
        try(MockedStatic<KeycloakModelUtils> modelUtilsMockedStatic = createKeycloakUtilsStaticMock()) {
            authenticator.authenticate(context);
            verify(context).success();
            verify(context.getUser()).addRequiredAction(eq(MccOtpConfigureSmsAction.PROVIDER_ID));
        }
    }

    @Test
    void actionSuccedsIfCodeIsCorrect() {
        setupActionMocks();
        try(MockedStatic<SmsProviderFactory> smsProviderFactoryMockedStatic = createSmsProviderFactoryStaticMock(true)) {
            authenticator.action(context);
            verify(context).success();
        }
    }

    @Test
    void actionSuccedsIfCodeIsIncorrect() {
        setupActionMocks();
        try(MockedStatic<SmsProviderFactory> smsProviderFactoryMockedStatic = createSmsProviderFactoryStaticMock(false)) {
            authenticator.action(context);
            verify(context).failure(eq(AuthenticationFlowError.EXPIRED_CODE));
        }
    }

    @Test
    void requiresUser() {
        assertThat(authenticator.requiresUser(), is(true));
    }

    @Test
    void configuredFor() {
        assertThat(authenticator.configuredFor(mock(KeycloakSession.class), mock(RealmModel.class), mock(UserModel.class)), is(true));
    }
}