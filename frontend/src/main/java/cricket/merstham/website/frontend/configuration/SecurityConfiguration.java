package cricket.merstham.website.frontend.configuration;

import cricket.merstham.website.frontend.security.filters.CognitoAuthenticationFailureHandler;
import cricket.merstham.website.frontend.security.filters.CognitoChallengeFilter;
import cricket.merstham.website.frontend.security.filters.CognitoChallengeResponseFilter;
import cricket.merstham.website.frontend.security.filters.CognitoExceptionTranslationFilter;
import cricket.merstham.website.frontend.security.providers.CognitoRefreshTokenAuthenticationProvider;
import cricket.merstham.website.frontend.security.providers.CognitoUsernamePasswordAuthenticationProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.header.writers.CrossOriginOpenerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static cricket.merstham.website.frontend.controller.LoginController.LOGIN_PROCESSING_URL;
import static cricket.merstham.website.frontend.controller.LoginController.LOGIN_URL;
import static cricket.merstham.website.frontend.controller.administration.CkFinderController.CONNECTOR_PATH;
import static java.util.Objects.nonNull;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    @Bean
    public CognitoIdentityProviderClient cognitoIdentityProviderClient(
            @Value("${spring.security.oauth2.client.registration.login.region:#{null}}")
                    String region) {
        return CognitoIdentityProviderClient.builder().region(Region.of(region)).build();
    }

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new HttpSessionOAuth2AuthorizedClientRepository();
    }

    @Bean
    public AuthenticationManager authManager(
            CognitoUsernamePasswordAuthenticationProvider
                    cognitoUsernamePasswordAuthenticationProvider,
            CognitoRefreshTokenAuthenticationProvider cognitoRefreshTokenAuthenticationProvider) {
        return new ProviderManager(
                cognitoUsernamePasswordAuthenticationProvider,
                cognitoRefreshTokenAuthenticationProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            CognitoChallengeFilter cognitoChallengeFilter,
            CognitoChallengeResponseFilter cognitoChallengeProcessingFilter,
            CognitoAuthenticationFailureHandler failureHandler,
            CognitoExceptionTranslationFilter cognitoExceptionTranslationFilter)
            throws Exception {
        http.csrf(
                        csrf ->
                                csrf.requireCsrfProtectionMatcher(
                                        new AndRequestMatcher(
                                                CsrfFilter.DEFAULT_CSRF_MATCHER,
                                                new NegatedRequestMatcher(
                                                        new AntPathRequestMatcher(
                                                                CONNECTOR_PATH)))))
                .headers(
                        headers ->
                                headers.frameOptions(Customizer.withDefaults())
                                        .crossOriginOpenerPolicy(
                                                cors ->
                                                        cors.policy(
                                                                CrossOriginOpenerPolicyHeaderWriter
                                                                        .CrossOriginOpenerPolicy
                                                                        .SAME_ORIGIN)))
                .exceptionHandling(
                        exceptionHandling ->
                                exceptionHandling.accessDeniedPage("/login?error=access_denied"))
                .sessionManagement(
                        session ->
                                session.sessionAuthenticationStrategy(
                                        sessionAuthenticationStrategy()))
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/"))
                .formLogin(
                        form ->
                                form.loginPage(LOGIN_URL)
                                        .loginProcessingUrl(LOGIN_PROCESSING_URL)
                                        .usernameParameter("email")
                                        .passwordParameter("password")
                                        .defaultSuccessUrl("/")
                                        .failureHandler(failureHandler))
                .addFilterBefore(cognitoChallengeFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(cognitoChallengeProcessingFilter, CognitoChallengeFilter.class)
                .addFilterBefore(
                        cognitoExceptionTranslationFilter, CognitoChallengeResponseFilter.class)
                .authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(
                    authority -> {
                        if (OidcUserAuthority.class.isInstance(authority)) {
                            OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;

                            OidcIdToken idToken = oidcUserAuthority.getIdToken();
                            var groups = (List<String>) idToken.getClaims().get("cognito:groups");
                            if (nonNull(groups))
                                groups.forEach(
                                        s ->
                                                mappedAuthorities.add(
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_"
                                                                        + s.toUpperCase(
                                                                                Locale.ROOT))));
                        } else if (OAuth2UserAuthority.class.isInstance(authority)) {
                            OAuth2UserAuthority oauth2UserAuthority =
                                    (OAuth2UserAuthority) authority;

                            Map<String, Object> userAttributes =
                                    oauth2UserAuthority.getAttributes();
                        }
                    });

            return mappedAuthorities;
        };
    }
}
