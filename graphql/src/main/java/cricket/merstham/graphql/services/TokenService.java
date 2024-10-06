package cricket.merstham.graphql.services;

import cricket.merstham.graphql.configuration.TokenConfiguration;
import cricket.merstham.graphql.configuration.VaultConfiguration;
import cricket.merstham.shared.dto.AuthRequest;
import cricket.merstham.shared.dto.AuthResult;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;

@Component
public class TokenService {
    private static final Logger LOG = LoggerFactory.getLogger(TokenService.class);
    private static final String HAS_SYSTEM_ROLE = "hasRole('ROLE_SYSTEM')";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret"; // pragma: allowlist secret
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String GRANT_TYPE = "grant_type";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String CODE_CHALLENGE = "code_challenge";
    public static final String CODE_CHALLENGE_METHOD = "code_challenge_method";
    public static final String PKCE_SHA_256 = "S256";
    public static final String CODE = "code";
    public static final String TOKEN_URL = "token_url";

    private final TokenConfiguration tokenConfiguration;
    private final VaultService vaultService;
    private final OAuthHelperService helper;
    private final Client tokenClient;
    private final String environment;
    private final VaultConfiguration vaultConfiguration;

    @Autowired
    public TokenService(
            TokenConfiguration tokenConfiguration,
            VaultService vaultService,
            OAuthHelperService helper,
            @Named("token-client") Client tokenClient,
            @Value("${configuration.environment}") String environment,
            VaultConfiguration vaultConfiguration) {
        this.tokenConfiguration = tokenConfiguration;
        this.vaultService = vaultService;
        this.helper = helper;
        this.tokenClient = tokenClient;
        this.environment = environment;
        this.vaultConfiguration = vaultConfiguration;
    }

    @PreAuthorize(HAS_SYSTEM_ROLE)
    public AuthRequest getAuthRequest(String name, String redirectUrl) {
        var config = tokenConfiguration.getTokens().get(name);
        if (isNull(config)) throw new RuntimeException("Token config not found");

        var vault =
                vaultService.write(
                        format("{0}/auth-code-url", tokenConfiguration.getVaultPath()),
                        Map.of(
                                "server", config.getServer(),
                                "state", helper.getState(name),
                                "scopes", config.getScopes(),
                                "redirect_url", redirectUrl));

        var url = UriBuilder.fromUri((String) vault.get("url"));
        if (config.isPkce()) {
            var verifier = helper.getPkceCodeVerifier(name);

            url.queryParam(CODE_CHALLENGE, helper.getPkceCodeChallenge(verifier));
            url.queryParam(CODE_CHALLENGE_METHOD, PKCE_SHA_256);
        }
        return AuthRequest.builder().name(name).url(url.build()).build();
    }

    @PreAuthorize(HAS_SYSTEM_ROLE)
    public AuthResult putAuthCode(String name, String code, String state, String redirectUrl) {
        var config = tokenConfiguration.getTokens().get(name);
        if (isNull(config)) throw new RuntimeException("Token config not found");
        if (!helper.getState(name).equals(state)) {
            return AuthResult.builder()
                    .name(name)
                    .success(false)
                    .message("Invalid/expired state received!")
                    .build();
        }

        var tokenRequestParms = new Form();
        var credentials =
                parseCredentials(
                        vaultService.get(
                                format(
                                        "{0}/{1}-credentials",
                                        tokenConfiguration.getCredentialsPath(), name)));
        var serverConfig =
                vaultService.get(
                        format("{0}/servers/{1}", tokenConfiguration.getVaultPath(), name));

        tokenRequestParms.param(CODE, code);
        tokenRequestParms.param(GRANT_TYPE, "authorization_code");
        tokenRequestParms.param(REDIRECT_URI, redirectUrl);

        if (config.isPkce()) {
            tokenRequestParms.param("code_verifier", helper.getPkceCodeVerifier(name));
        }
        var message = "";
        helper.clear(name);
        try {
            var response =
                    makeTokenRequest(getTokenUrl(serverConfig), credentials, tokenRequestParms);
            vaultService.write(
                    format(
                            "{0}/creds/{1}-{2}",
                            tokenConfiguration.getVaultPath(), environment, name),
                    Map.of(
                            "server",
                            name,
                            GRANT_TYPE,
                            REFRESH_TOKEN,
                            REFRESH_TOKEN,
                            response.get(REFRESH_TOKEN)));
            return AuthResult.builder()
                    .name(name)
                    .success(true)
                    .message("Successfully stored token")
                    .build();
        } catch (BadRequestException ex) {
            LOG.error(
                    "Error response ({}) from token endpoint: {}",
                    ex.getResponse().getStatus(),
                    ex.getResponse().readEntity(String.class));
            message = ex.getMessage();
        } catch (Exception ex) {
            LOG.error("Error calling token endpoint", ex);
            message = ex.getMessage();
        }
        return AuthResult.builder().name(name).success(false).message(message).build();
    }

    private Map<String, String> parseCredentials(Map<String, Object> response) {
        if (response.containsKey("data")) {
            return (Map<String, String>) response.get("data");
        }
        return Map.of();
    }

    private Map<String, Object> makeTokenRequest(
            URI tokenUrl, Map<String, String> credentials, Form tokenRequestParms) {
        LOG.info(
                "Making token request to {}; client_id = {}", credentials.get(CLIENT_ID), tokenUrl);
        return tokenClient
                .invocation(Link.fromUri(tokenUrl).build())
                .header("Authorization", encodeCredentials(credentials))
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .buildPost(Entity.form(tokenRequestParms))
                .invoke(new GenericType<>() {});
    }

    private String encodeCredentials(Map<String, String> credentials) {
        var encoded =
                Base64.getEncoder()
                        .encodeToString(
                                format(
                                                "{0}:{1}",
                                                credentials.get(CLIENT_ID),
                                                credentials.get(CLIENT_SECRET))
                                        .getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }

    private URI getTokenUrl(Map<String, Object> serverConfig) {
        if (serverConfig.containsKey("provider_options")) {
            Map<String, Object> options =
                    (Map<String, Object>) serverConfig.get("provider_options");
            return URI.create((String) options.get(TOKEN_URL));
        }
        return null;
    }

    public String getToken(String name) {
        var result =
                vaultService.get(
                        format(
                                "{0}/creds/{1}-{2}",
                                tokenConfiguration.getVaultPath(), environment, name));
        return (String) result.get("access_token");
    }
}
