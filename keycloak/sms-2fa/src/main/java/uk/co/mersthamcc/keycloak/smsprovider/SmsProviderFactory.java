package uk.co.mersthamcc.keycloak.smsprovider;

import com.messagebird.MessageBirdClient;
import com.messagebird.MessageBirdService;
import com.messagebird.MessageBirdServiceImpl;
import org.jboss.logging.Logger;
import uk.co.mersthamcc.keycloak.authenticator.KeycloakConfigurableTwoFactorAuthenticatorFactory;
import uk.co.mersthamcc.keycloak.smsprovider.messagebird.MessageBirdSmsProvider;

import java.util.Map;

public class SmsProviderFactory {

    private static Logger logger = Logger.getLogger(SmsProviderFactory.class);

    public static SmsProvider create(Map<String, String> config) {

        SmsProviders provider = SmsProviders.valueOf(config.get(KeycloakConfigurableTwoFactorAuthenticatorFactory.CONFIG_PROPERTY_SMS_PROVIDER));

        switch (provider) {
            case MESSAGEBIRD:
                String accessToken = config.get(KeycloakConfigurableTwoFactorAuthenticatorFactory.CONFIG_PROPERTY_API_KEY);
                MessageBirdService service = new MessageBirdServiceImpl(accessToken);
                return new MessageBirdSmsProvider(new MessageBirdClient(service));
        }
        return null;
    }
}
