package cricket.merstham.website.frontend.service;

import com.nimbusds.oauth2.sdk.ClientCredentialsGrant;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import cricket.merstham.website.frontend.exception.AccessTokenRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

@Service
public class AccessTokenManager {

    private static final Logger LOG = LoggerFactory.getLogger(AccessTokenManager.class);

    private final TokenCache tokenCache;
    private final ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    public AccessTokenManager(
            TokenCache tokenCache, ClientRegistrationRepository clientRegistrationRepository) {
        this.tokenCache = tokenCache;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    public String getAccessToken() {
        return tokenCache
                .getAccessToken()
                .orElseGet(
                        () -> {
                            var accessToken = requestAccessToken();
                            return tokenCache
                                    .updateToken(
                                            accessToken.getValue(),
                                            Duration.of(accessToken.getLifetime() - 60, SECONDS))
                                    .getAccessToken()
                                    .get();
                        });
    }

    private AccessToken requestAccessToken() {
        LOG.info("Getting new client credentials access token");

        var client = clientRegistrationRepository.findByRegistrationId("website");
        TokenRequest request =
                new TokenRequest(
                        URI.create(client.getProviderDetails().getTokenUri()),
                        new ClientSecretBasic(
                                new ClientID(client.getClientId()),
                                new Secret(client.getClientSecret())),
                        new ClientCredentialsGrant(),
                        Scope.parse(client.getScopes()));

        try {
            TokenResponse response = TokenResponse.parse(request.toHTTPRequest().send());
            if (response.indicatesSuccess()) {
                var accessToken = response.toSuccessResponse().getTokens().getAccessToken();
                LOG.debug("Successfully got client access token: {}", accessToken.toJSONString());
                return accessToken;
            }
            LOG.error(
                    "Failed to get client credentials access token: {}",
                    response.toHTTPResponse().getContent());
            throw new AccessTokenRequestException(response.toHTTPResponse().getContent());
        } catch (IOException | ParseException e) {
            LOG.error("Failed to get client credentials access token", e);
            throw new AccessTokenRequestException("Failed to get client credentials access token");
        }
    }
}
