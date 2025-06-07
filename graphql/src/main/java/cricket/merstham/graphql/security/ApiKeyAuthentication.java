package cricket.merstham.graphql.security;

import lombok.Builder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Builder
public class ApiKeyAuthentication implements Authentication {

    private final String name;
    private final String apiKey;
    private final Collection<? extends GrantedAuthority> authorities;

    public ApiKeyAuthentication(
            String name, String apiKey, Collection<? extends GrantedAuthority> authorities) {
        this.name = name;
        this.apiKey = apiKey;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return apiKey;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return name;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        return name;
    }
}
