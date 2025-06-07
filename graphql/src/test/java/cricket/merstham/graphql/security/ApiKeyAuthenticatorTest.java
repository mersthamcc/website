package cricket.merstham.graphql.security;

import cricket.merstham.graphql.configuration.ApiKey;
import cricket.merstham.graphql.configuration.ApiKeyConfig;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

class ApiKeyAuthenticatorTest {

    private static final String HEADER = "API-KEY";
    private static final ApiKeyConfig CONFIG =
            ApiKeyConfig.builder()
                    .headerName(HEADER)
                    .keys(
                            List.of(
                                    ApiKey.builder()
                                            .name("api-client")
                                            .key("api-key")
                                            .trusted(false)
                                            .build(),
                                    ApiKey.builder()
                                            .name("trusted-api-client")
                                            .key("trusted-api-key")
                                            .trusted(true)
                                            .build()))
                    .build();

    private final MeterRegistry meterRegistry = new SimpleMeterRegistry();
    private final ApiKeyAuthenticator auth = new ApiKeyAuthenticator(CONFIG, meterRegistry);

    @Test
    void shouldReturnNullWhenNoApiKeyProvided() {
        var request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        var result = auth.authenticate(request);

        assertThat(result).isNull();
    }

    @Test
    void shouldThrowExceptionWhenInvalidApiKeyProvided() {
        var request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader(HEADER, "invalid");

        assertThrows(BadCredentialsException.class, () -> auth.authenticate(request));
    }

    @Test
    void shouldReturnApiKeyAuthenticationWhenValidApiKeyProvided() {
        var request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader(HEADER, "api-key");

        var result = auth.authenticate(request);

        assertThat(result).isNotNull().isInstanceOf(ApiKeyAuthentication.class);
        assertThat(result.getName()).isEqualTo("api-client");
        assertThat(result.getAuthorities()).isEmpty();
    }

    @Test
    void shouldReturnApiKeyAuthenticationWhenTrustedApiKeyProvided() {
        var request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader(HEADER, "trusted-api-key");

        var result = auth.authenticate(request);

        assertThat(result).isNotNull().isInstanceOf(ApiKeyAuthentication.class);
        assertThat(result.getName()).isEqualTo("trusted-api-client");
        assertThat(result.getAuthorities()).hasSize(1);
        var authority = result.getAuthorities().iterator().next();
        assertThat(authority).isInstanceOf(SimpleGrantedAuthority.class);
        assertThat(authority.getAuthority()).isEqualTo("TRUSTED_CLIENT");
    }
}
