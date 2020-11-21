package uk.co.mersthamcc.keycloak.authenticator;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import uk.co.mersthamcc.keycloak.smsprovider.SmsProviders;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.keycloak.authentication.authenticators.browser.ConditionalOtpFormAuthenticator.FORCE_OTP_ROLE;
import static org.keycloak.provider.ProviderConfigProperty.ROLE_TYPE;

public class KeycloakConfigurableTwoFactorAuthenticatorFactory implements AuthenticatorFactory {

    public static final String CONFIG_PROPERTY_API_KEY = "mcc.mcc-2fa.api-key";
    public static final String CONFIG_PROPERTY_SMS_PROVIDER = "mcc.mcc-2fa.sms-provider";
    public static final String CONFIG_PROPERTY_FORCE_OTP_ROLE = "forceOtpRole";


    private static final String PROVIDER_ID = "mcc-two-factor-authentication";
    private static KeycloakConfigurableTwoFactorAuthenticator SINGLETON_INSTANCE;
    private static final Logger logger = Logger.getLogger(KeycloakConfigurableTwoFactorAuthenticatorFactory.class);
    private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.ALTERNATIVE,
            AuthenticationExecutionModel.Requirement.DISABLED
    };

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<ProviderConfigProperty>();

    static {
        ProviderConfigProperty property;

        property = new ProviderConfigProperty();
        property.setName(CONFIG_PROPERTY_SMS_PROVIDER);
        property.setLabel("SMS provider");
        property.setHelpText("Select SMS provider");
        property.setType(ProviderConfigProperty.LIST_TYPE);
        property.setDefaultValue(SmsProviders.MESSAGEBIRD);
        property.setOptions(Stream.of(SmsProviders.values())
                .map(Enum::name)
                .collect(Collectors.toList()));
        configProperties.add(property);

        property = new ProviderConfigProperty();
        property.setName(CONFIG_PROPERTY_API_KEY);
        property.setLabel("SMS Provider API Key");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("The SMS Provider API Key.");
        configProperties.add(property);

        property = new ProviderConfigProperty();
        property.setType(ROLE_TYPE);
        property.setName(CONFIG_PROPERTY_FORCE_OTP_ROLE);
        property.setLabel("Force OTP for Role");
        property.setHelpText("OTP is always required if user has the given Role.");
        configProperties.add(property);
    }

        @Override
    public String getDisplayType() {
        return "MCC 2FA Authentication with SMS";
    }

    @Override
    public String getReferenceCategory() {
        return "mcc-2fa";
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return true;
    }

    @Override
    public String getHelpText() {
        return "Validates an OTP sent by SMS or an authenticator app";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        if (SINGLETON_INSTANCE == null) {
            SINGLETON_INSTANCE = new KeycloakConfigurableTwoFactorAuthenticator();
        }
        return SINGLETON_INSTANCE;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
