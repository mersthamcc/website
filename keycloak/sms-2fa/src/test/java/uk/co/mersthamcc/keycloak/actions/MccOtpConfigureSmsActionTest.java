package uk.co.mersthamcc.keycloak.actions;

import org.jboss.resteasy.spi.HttpRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.UserModel;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.mersthamcc.keycloak.authenticator.KeycloakConfigurableTwoFactorAuthenticator;
import uk.co.mersthamcc.keycloak.helpers.MccOtpSmsHelper;
import uk.co.mersthamcc.keycloak.smsprovider.SmsProvider;
import uk.co.mersthamcc.keycloak.smsprovider.SmsProviderFactory;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static uk.co.mersthamcc.keycloak.TestHelpers.MOBILE_NUMBER;
import static uk.co.mersthamcc.keycloak.TestHelpers.createSmsProviderFactoryStaticMock;
import static uk.co.mersthamcc.keycloak.actions.MccOtpConfigureSmsAction.CONFIGURE_SMS_FORM;
import static uk.co.mersthamcc.keycloak.actions.MccOtpConfigureSmsAction.OTP_FIELD;

@ExtendWith(MockitoExtension.class)
class MccOtpConfigureSmsActionTest {

    MccOtpConfigureSmsAction action = new MccOtpConfigureSmsAction();

    @Mock
    RequiredActionContext context;

    @Mock
    UserModel user;

    @Mock
    LoginFormsProvider provider;

    @Mock
    Response smsConfigureForm;

    private void setupChallengeMocks() {
        when(context.getUser()).thenReturn(user);
        when(context.form()).thenReturn(provider);
    }

    private void setupActionMocks(MultivaluedMap<String, String> form, boolean mockSession) {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getDecodedFormParameters()).thenReturn(form);
        when(context.getHttpRequest()).thenReturn(request);
        if (mockSession) when(context.getAuthenticationSession()).thenReturn(mock(AuthenticationSessionModel.class));
    }
    @Test
    void requiredActionChallengeWhenUserHasAPhoneNumberConfigured() {
        setupChallengeMocks();
        when(user.getFirstAttribute(eq(KeycloakConfigurableTwoFactorAuthenticator.MOBILE_PHONE_ATTR))).thenReturn(MOBILE_NUMBER);
        when(provider.setAttribute(eq(MccOtpConfigureSmsAction.PHONE_NUMBER_TEMPLATE_ATTRIBUTE), eq(MOBILE_NUMBER))).thenReturn(provider);
        when(provider.createForm(eq(MccOtpConfigureSmsAction.CONFIGURE_SMS_FORM))).thenReturn(smsConfigureForm);

        action.requiredActionChallenge(context);
        verify(context).challenge(eq(smsConfigureForm));
    }

    @Test
    void processActionUpdatePhoneNumberAndSendVerification() {
        setupChallengeMocks();
        when(provider.setAttribute(eq(MccOtpConfigureSmsAction.PHONE_NUMBER_TEMPLATE_ATTRIBUTE), isNull())).thenReturn(provider);
        when(provider.createForm(eq(MccOtpConfigureSmsAction.CONFIGURE_SMS_FORM))).thenReturn(smsConfigureForm);

        action.requiredActionChallenge(context);
        verify(context).challenge(eq(smsConfigureForm));
    }

    @Test
    void processActionCheckVerificationAndSucceed() {
        try(MockedStatic<SmsProviderFactory> factoryMockedStatic = createSmsProviderFactoryStaticMock(true)) {
            MultivaluedMap<String, String> form = new MultivaluedHashMap<>();
            form.put(OTP_FIELD, List.of("123456"));
            setupActionMocks(form, true);
            action.processAction(context);
            verify(context).success();
        }
    }

    @Test
    void processActionCheckVerificationAndFail() {
        try(MockedStatic<SmsProviderFactory> factoryMockedStatic = createSmsProviderFactoryStaticMock(false)) {
            MultivaluedMap<String, String> form = new MultivaluedHashMap<>();
            form.put(OTP_FIELD, List.of("123456"));
            setupActionMocks(form, true);
            action.processAction(context);
            verify(context).failure();
        }
    }

    @Test
    void processActionUpdatePhoneNumberAndChallengeForVerification() {
        setupChallengeMocks();
        try(MockedStatic<SmsProviderFactory> utilsMockedStatic = mockStatic(SmsProviderFactory.class)) {
            SmsProvider smsProvider = mock(SmsProvider.class);
            utilsMockedStatic.when(SmsProviderFactory::create).thenReturn(smsProvider);

            try (MockedStatic<MccOtpSmsHelper> helperMockedStatic = mockStatic(MccOtpSmsHelper.class)) {
                MultivaluedMap<String, String> form = new MultivaluedHashMap<>();
                form.put(MccOtpConfigureSmsAction.PHONE_NUMBER_FIELD, List.of(MOBILE_NUMBER));
                helperMockedStatic.when(() -> MccOtpSmsHelper.processUpdate(eq(user), eq(form))).thenReturn(true);
                when(user.getFirstAttribute(eq(KeycloakConfigurableTwoFactorAuthenticator.MOBILE_PHONE_ATTR))).thenReturn(MOBILE_NUMBER);
                setupActionMocks(form, true);
                Response verifyForm = mock(Response.class);
                when(context.form()).thenReturn(provider);
                when(provider.createLoginTotp()).thenReturn(verifyForm);

                action.processAction(context);
                verify(smsProvider).send(any(AuthenticationSessionModel.class), eq(MOBILE_NUMBER));
                verify(context).challenge(eq(verifyForm));
            }
        }
    }

    @Test
    void processActionUpdatePhoneNumberFails() {
        setupChallengeMocks();
        try(MockedStatic<SmsProviderFactory> utilsMockedStatic = mockStatic(SmsProviderFactory.class)) {
            SmsProvider smsProvider = mock(SmsProvider.class);
            utilsMockedStatic.when(SmsProviderFactory::create).thenReturn(smsProvider);

            try (MockedStatic<MccOtpSmsHelper> helperMockedStatic = mockStatic(MccOtpSmsHelper.class)) {
                MultivaluedMap<String, String> form = new MultivaluedHashMap<>();
                form.put(MccOtpConfigureSmsAction.PHONE_NUMBER_FIELD, List.of(MOBILE_NUMBER));
                helperMockedStatic.when(() -> MccOtpSmsHelper.processUpdate(eq(user), eq(form))).thenReturn(false);
                setupActionMocks(form, false);
                when(context.form()).thenReturn(provider);
                when(provider.setError(anyString(), any())).thenReturn(provider);
                when(provider.createForm(eq(CONFIGURE_SMS_FORM))).thenReturn(smsConfigureForm);

                action.processAction(context);

                verify(context).challenge(eq(smsConfigureForm));
            }
        }
    }
}