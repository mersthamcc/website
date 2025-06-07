package cricket.merstham.graphql.security;

import cricket.merstham.graphql.configuration.ApiKey;
import cricket.merstham.graphql.configuration.ApiKeyConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.nonNull;

@Component
public class ApiKeyAuthenticator {
    private static final Logger LOG = LoggerFactory.getLogger(ApiKeyAuthenticator.class);

    private final ApiKeyConfig apiKeyConfig;

    @Autowired
    public ApiKeyAuthenticator(ApiKeyConfig apiKeyConfig) {
        this.apiKeyConfig = apiKeyConfig;
    }

    public Authentication authenticate(HttpServletRequest request) {
        var apiKeyInHeader = request.getHeader(apiKeyConfig.getHeaderName());
        if (nonNull(apiKeyInHeader)) {
            try {
                var apiKey = matchApiKey(apiKeyInHeader);
                if (nonNull(apiKey)) {
                    List<GrantedAuthority> authorities = List.of();
                    if (apiKey.isTrusted()) {
                        authorities = List.of(new SimpleGrantedAuthority("TRUSTED_CLIENT"));
                    }
                    LOG.info(
                            "Authenticated api key client '{}' from {}, trusted = {}",
                            apiKey.getName(),
                            request.getRemoteAddr(),
                            apiKey.isTrusted());
                    return ApiKeyAuthentication.builder()
                            .name(apiKey.getName())
                            .apiKey(apiKey.getKey())
                            .authorities(authorities)
                            .build();
                }
            } catch (BadCredentialsException ex) {
                LOG.warn(
                        "Api key authentication rejected '{}' from {}",
                        apiKeyInHeader,
                        request.getRemoteAddr());
                throw ex;
            }
        }
        return null;
    }

    private ApiKey matchApiKey(String key) {
        return apiKeyConfig.getKeys().stream()
                .filter(f -> f.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new BadCredentialsException("Invalid api key"));
    }
}
