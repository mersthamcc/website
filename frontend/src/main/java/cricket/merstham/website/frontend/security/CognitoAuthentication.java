package cricket.merstham.website.frontend.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.SignedJWT;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.io.Serial;
import java.text.ParseException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;
import static java.util.Objects.nonNull;

@Getter
@EqualsAndHashCode
@ToString
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class CognitoAuthentication implements Authentication {

    @Serial private static final long serialVersionUID = 6593483641709615121L;
    private static final String GROUP_CLAIM = "cognito:groups";

    private String accessToken;
    private OAuth2AccessToken oAuth2AccessToken;
    private final JWT accessTokenJwt;
    private final String refreshToken;
    private final String idToken;
    private final JWT idTokenJwt;
    private final String challenge;
    private final List<GrantedAuthority> authorities;
    private final OidcUser oidcUser;

    public CognitoAuthentication(
            String accessToken, String refreshToken, String idToken, String challenge) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.idToken = idToken;

        this.challenge = challenge;
        try {
            this.accessTokenJwt = SignedJWT.parse(accessToken);
            this.idTokenJwt = SignedJWT.parse(idToken);
            var claimSet = idTokenJwt.getJWTClaimsSet();

            if (nonNull(claimSet.getStringListClaim(GROUP_CLAIM))) {
                this.authorities =
                        claimSet.getStringListClaim(GROUP_CLAIM).stream()
                                .map(r -> new SimpleGrantedAuthority(format("ROLE_{0}", r)))
                                .collect(Collectors.toList());
            } else {
                this.authorities = List.of();
            }
            this.oidcUser =
                    new DefaultOidcUser(
                            this.authorities,
                            new OidcIdToken(
                                    idToken,
                                    claimSet.getIssueTime().toInstant(),
                                    claimSet.getExpirationTime().toInstant(),
                                    claimSet.getClaims()));
            this.oAuth2AccessToken =
                    new OAuth2AccessToken(
                            OAuth2AccessToken.TokenType.BEARER,
                            this.accessToken,
                            this.accessTokenJwt.getJWTClaimsSet().getIssueTime().toInstant(),
                            this.accessTokenJwt.getJWTClaimsSet().getExpirationTime().toInstant());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return accessToken;
    }

    @Override
    public Object getDetails() {
        return idToken;
    }

    @Override
    public Object getPrincipal() {
        return oidcUser;
    }

    @Override
    public boolean isAuthenticated() {
        try {
            return nonNull(accessToken)
                    && accessTokenJwt
                            .getJWTClaimsSet()
                            .getExpirationTime()
                            .toInstant()
                            .isAfter(Instant.now());
        } catch (ParseException e) {
            return false;
        }
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (!isAuthenticated) {
            this.accessToken = null;
        }
    }

    @Override
    public String getName() {
        try {
            return (String) idTokenJwt.getJWTClaimsSet().getClaim("email");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
