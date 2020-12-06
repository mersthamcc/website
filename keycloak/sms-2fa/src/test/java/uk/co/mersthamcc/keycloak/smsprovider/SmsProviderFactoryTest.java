package uk.co.mersthamcc.keycloak.smsprovider;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import uk.co.mersthamcc.keycloak.smsprovider.dummy.DummySmsProvider;
import uk.co.mersthamcc.keycloak.smsprovider.messagebird.MessageBirdSmsProvider;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


class SmsProviderFactoryTest {

    @Test
    void createWithNoConfigProducesDummy() {
        SmsProvider provider = SmsProviderFactory.create();
        assertThat(DummySmsProvider.class, equalTo(provider.getClass()));
    }

    @Test
    @SetEnvironmentVariable(key = SmsProviderFactory.SMS_PROVIDER_ENVIRONMENT_VARIABLE, value = "MESSAGEBIRD")
    void createWithMessageProvider() {
        SmsProvider provider = SmsProviderFactory.create();
        assertThat(MessageBirdSmsProvider.class, equalTo(provider.getClass()));
    }
}