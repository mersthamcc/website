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
import javax.ws.rs.client.*;
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
                            AccessTokenResponse accessTokenResponse = requestAccessToken();
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

        ServerConfiguration configuration = getServerConfiguration();

        Client client = ClientBuilder.newClient();
        client.register(HttpAuthenticationFeature.basic(clientId, clientSecret));
        WebTarget target = client.target(configuration.getTokenEndpoint());
        Invocation.Builder requestBuilder = target.request(MediaType.APPLICATION_JSON_TYPE);

        Form form = new Form();
        form.param("grant_type", "client_credentials");

        Response response = requestBuilder.build(HttpMethod.POST, Entity.form(form)).invoke();
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            AccessTokenResponse accessToken = response.readEntity(AccessTokenResponse.class);
            LOG.debug("Successfully got client access token: {}", accessToken.getToken());
            return accessToken;
        }
        LOG.error("Failed to get client credentials access token");
        throw new AccessTokenRequestException("Failed to get client credentials access token");
    }

    private ServerConfiguration getServerConfiguration() {
        String configurationUrl =
                KeycloakUriBuilder.fromUri(authServerUrl)
                        .clone()
                        .path(AUTHZ_DISCOVERY_URL)
                        .build(realm)
                        .toString();
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(configurationUrl);
        Invocation.Builder requestBuilder = target.request(MediaType.APPLICATION_JSON_TYPE);
        requestBuilder.accept(MediaType.APPLICATION_JSON_TYPE);
        return requestBuilder.get().readEntity(ServerConfiguration.class);
    }
}
