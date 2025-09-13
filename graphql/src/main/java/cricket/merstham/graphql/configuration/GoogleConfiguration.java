package cricket.merstham.graphql.configuration;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.walletobjects.WalletobjectsScopes;
import com.google.auth.oauth2.GoogleCredentials;
import cricket.merstham.graphql.services.VaultService;
import jakarta.inject.Named;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.utils.StringInputStream;

import java.io.IOException;
import java.util.List;

@Configuration
public class GoogleConfiguration {

    @Bean
    @Named("WalletCredentials")
    public GoogleCredentials getGoogleWalletCredentials(VaultService vaultService)
            throws IOException {
        var secret = vaultService.getEnvironmentSecret("google_credentials");
        var credentials =
                GoogleCredentials.fromStream(new StringInputStream(secret))
                        .createScoped(List.of(WalletobjectsScopes.WALLET_OBJECT_ISSUER));
        credentials.refresh();
        return credentials;
    }

    @Bean
    @Named("SheetsCredentials")
    public GoogleCredentials getGoogleSheetsCredentials(VaultService vaultService)
            throws IOException {
        var secret = vaultService.getEnvironmentSecret("google_credentials");
        var credentials =
                GoogleCredentials.fromStream(new StringInputStream(secret))
                        .createScoped(List.of(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE));
        credentials.refresh();
        return credentials;
    }

    @Bean
    @Named("CalendarCredentials")
    public GoogleCredentials getGoogleCalendarCredentials(VaultService vaultService)
            throws IOException {
        var secret = vaultService.getEnvironmentSecret("google_credentials");
        var credentials =
                GoogleCredentials.fromStream(new StringInputStream(secret))
                        .createScoped(List.of(CalendarScopes.CALENDAR_EVENTS));
        credentials.refresh();
        return credentials;
    }
}
