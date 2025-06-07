package cricket.merstham.graphql.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApiKeyAuthenticationFilterTest {

    private final FilterChain filterChain = mock(FilterChain.class);
    private final ApiKeyAuthenticator auth = mock(ApiKeyAuthenticator.class);
    private final ApiKeyAuthenticationFilter authFilter = new ApiKeyAuthenticationFilter(auth);
    private final MockHttpServletRequest request = new MockHttpServletRequest();

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
        request.setRemoteAddr("127.0.0.1");
    }

    @Test
    void shouldAttemptAuthenticateWhenNoExistingSecurityContext()
            throws ServletException, IOException {
        var response = new MockHttpServletResponse();
        authFilter.doFilterInternal(request, response, filterChain);

        verify(auth).authenticate(request);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldAttemptAuthenticateWhenSecurityContextHasNoExistingAuthentication()
            throws ServletException, IOException {
        var response = new MockHttpServletResponse();
        var context = SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.setContext(context);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(auth).authenticate(request);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldSetAuthenticationWhenSecurityContextHasNoExistingAuthenticationAndAuthSucceeds()
            throws ServletException, IOException {
        var response = new MockHttpServletResponse();
        var context = SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.setContext(context);
        var authentication =
                ApiKeyAuthentication.builder()
                        .apiKey("apiKey")
                        .name("name")
                        .authorities(List.of())
                        .build();
        when(auth.authenticate(request)).thenReturn(authentication);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(auth).authenticate(request);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isEqualTo(authentication);
    }

    @Test
    void shouldNotAttemptAuthenticateWhenSecurityContextHasExistingAuthentication()
            throws ServletException, IOException {
        var response = new MockHttpServletResponse();
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken("username", "password"));
        SecurityContextHolder.setContext(context);

        authFilter.doFilterInternal(request, response, filterChain);

        verify(auth, never()).authenticate(request);
        verify(filterChain).doFilter(request, response);
    }
}
