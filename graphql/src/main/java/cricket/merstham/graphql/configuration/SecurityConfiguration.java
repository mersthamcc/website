package cricket.merstham.graphql.configuration;

import cricket.merstham.graphql.security.ApiKeyAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            Converter<Jwt, AbstractAuthenticationToken> grantedAuthoritiesExtractor,
            ApiKeyAuthenticationFilter apiKeyAuthenticationFilter)
            throws Exception {
        http.cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(
                        oauth2 ->
                                oauth2.jwt(
                                        jwt ->
                                                jwt.jwtAuthenticationConverter(
                                                        grantedAuthoritiesExtractor)))
                .addFilterAfter(apiKeyAuthenticationFilter, BearerTokenAuthenticationFilter.class)
                .authorizeHttpRequests(
                        requests ->
                                requests.requestMatchers("/webhooks/**")
                                        .permitAll()
                                        .requestMatchers("/passkit/**")
                                        .permitAll()
                                        .requestMatchers("/graphql")
                                        .permitAll()
                                        .requestMatchers("/actuator/**")
                                        .permitAll()
                                        .requestMatchers("/graphiql**")
                                        .anonymous());
        return http.build();
    }

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> grantedAuthoritiesExtractor(
            @Value("${configuration.trusted-client-scope}") String trustedScope) {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                new GrantedAuthoritiesExtractor(trustedScope));

        return jwtAuthenticationConverter;
    }

    static class GrantedAuthoritiesExtractor
            implements Converter<Jwt, Collection<GrantedAuthority>> {

        private final String trustedScope;

        GrantedAuthoritiesExtractor(String trustedScope) {
            this.trustedScope = trustedScope;
        }

        public Collection<GrantedAuthority> convert(Jwt jwt) {
            List<GrantedAuthority> result = new ArrayList<>();
            Collection<String> authorities =
                    (Collection<String>) jwt.getClaims().get("cognito:groups");

            if (authorities != null) {
                result.addAll(
                        authorities.stream()
                                .map(
                                        s ->
                                                (GrantedAuthority)
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_"
                                                                        + s.toUpperCase(
                                                                                Locale.ROOT)))
                                .toList());
            }

            Collection<String> scopes = jwt.getClaimAsStringList("scope");

            if (scopes != null && scopes.contains(trustedScope)) {
                result.add(new SimpleGrantedAuthority("SCOPE_TRUSTED"));
            }
            return result;
        }
    }
}
