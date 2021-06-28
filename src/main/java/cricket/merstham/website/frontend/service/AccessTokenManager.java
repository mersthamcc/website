package cricket.merstham.website.frontend.service;

import cricket.merstham.website.frontend.exception.AccessTokenRequestException;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.keycloak.authorization.client.representation.ServerConfiguration;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.keycloak.constants.ServiceUrlConstants.AUTHZ_DISCOVERY_URL;

@Service
public class AccessTokenManager {

    private static final Logger LOG = LoggerFactory.getLogger(AccessTokenManager.class);

    private final TokenCache tokenCache;
    private String realm;
    private String authServerUrl;
    private String clientId;
    private String clientSecret;

    @Autowired
    public AccessTokenManager(
            TokenCache tokenCache,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.auth-server-url}") String authServerUrl,
            @Value("${keycloak.resource}") String clientId,
            @Value("${keycloak.credentials.secret}") String clientSecret) {
        this.tokenCache = tokenCache;
        this.realm = realm;
        this.authServerUrl = authServerUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getAccessToken() {
        return tokenCache
                .getAccessToken()
                .orElseGet(
                        () -> {
                            var accessTokenResponse = requestAccessToken();
                            return tokenCache
                                    .updateToken(
                                            accessTokenResponse.getToken(),
                                            Duration.of(
                                                    accessTokenResponse.getExpiresIn() - 60,
                                                    SECONDS))
                                    .getAccessToken()
                                    .get();
                        });
    }

    private AccessTokenResponse requestAccessToken() {
        LOG.info("Getting new client credentials access token");

        var configuration = getServerConfiguration();

        var client = ClientBuilder.newClient();
        client.register(HttpAuthenticationFeature.basic(clientId, clientSecret));
        WebTarget target = client.target(configuration.getTokenEndpoint());
        var requestBuilder = target.request(MediaType.APPLICATION_JSON_TYPE);

        var form = new Form();
        form.param("grant_type", "client_credentials");

        var response = requestBuilder.build(HttpMethod.POST, Entity.form(form)).invoke();
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            var accessToken = response.readEntity(AccessTokenResponse.class);
            LOG.debug("Successfully got client access token: {}", accessToken.getToken());
            return accessToken;
        }
        LOG.error("Failed to get client credentials access token");
        throw new AccessTokenRequestException("Failed to get client credentials access token");
    }

    private ServerConfiguration getServerConfiguration() {
        var configurationUrl =
                KeycloakUriBuilder.fromUri(authServerUrl)
                        .clone()
                        .path(AUTHZ_DISCOVERY_URL)
                        .build(realm)
                        .toString();
        var client = ClientBuilder.newClient();
        var target = client.target(configurationUrl);
        return target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get().readEntity(ServerConfiguration.class);
    }
}
