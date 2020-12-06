package uk.co.mersthamcc.keycloak.smsprovider;

import org.jboss.logging.Logger;
import uk.co.mersthamcc.keycloak.smsprovider.dummy.DummySmsProvider;

import java.util.Optional;
import java.util.ServiceLoader;

import static java.lang.String.format;

public class SmsProviderFactory {

    private static final Logger logger = Logger.getLogger(SmsProviderFactory.class);

    private SmsProviderFactory() {
        // Not used
    }

    public static SmsProvider create() {
        ServiceLoader<SmsProvider> providerServiceLoader = ServiceLoader.load(SmsProvider.class, SmsProvider.class.getClassLoader());
        providerServiceLoader.reload();
        for (SmsProvider p : providerServiceLoader) {
            logger.info(format("Found SMS Provider %s", p.getClass().getName()));
        }
        logger.info(format("Found %d SMS Provider(s)", providerServiceLoader.stream().count()));
        String providerName = System.getenv().getOrDefault("SMS_OTP_PROVIDER", DummySmsProvider.PROVIDER_NAME);
        Optional<ServiceLoader.Provider<SmsProvider>> provider = providerServiceLoader.stream().filter(p -> p.get().getName().equals(providerName)).findFirst();
        return provider.isPresent() ? provider.get().get() : null;
    }
}
