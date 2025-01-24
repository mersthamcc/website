package cricket.merstham.graphql.configuration;

import com.google.api.services.walletobjects.WalletobjectsScopes;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.utils.StringInputStream;

import java.io.IOException;
import java.util.List;

@Configuration
public class GoogleConfiguration {

    @Bean
    public GoogleCredentials getGoogleCredentials(
            @Value("${configuration.wallet.google.credentials}") String credentialJson)
            throws IOException {
        var credentials =
                GoogleCredentials.fromStream(new StringInputStream(credentialJson))
                        .createScoped(List.of(WalletobjectsScopes.WALLET_OBJECT_ISSUER));
        credentials.refresh();
        return credentials;
    }
}
