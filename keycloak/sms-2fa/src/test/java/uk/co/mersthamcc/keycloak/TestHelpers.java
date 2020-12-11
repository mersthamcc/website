package uk.co.mersthamcc.keycloak;

import org.keycloak.sessions.AuthenticationSessionModel;
import org.mockito.MockedStatic;
import uk.co.mersthamcc.keycloak.smsprovider.SmsProvider;
import uk.co.mersthamcc.keycloak.smsprovider.SmsProviderFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TestHelpers {

    public static final String MOBILE_NUMBER = "+447777123456";

    public static MockedStatic<SmsProviderFactory> createSmsProviderFactoryStaticMock(boolean validateWillSucceed) {
        MockedStatic<SmsProviderFactory> utilsMockedStatic = mockStatic(SmsProviderFactory.class);
        SmsProvider provider = mock(SmsProvider.class);
        when(provider.validate(any(AuthenticationSessionModel.class), anyString())).thenReturn(validateWillSucceed);
        utilsMockedStatic.when(SmsProviderFactory::create).thenReturn(provider);
        return utilsMockedStatic;
    }
}
