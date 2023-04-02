package cricket.merstham.website.frontend.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class CognitoPasswordResetAuthentication implements Authentication {

    @Serial private static final long serialVersionUID = -3555473082889517710L;

    public enum Step {
        DEFAULT,
    }

    private final String sessionId;
    private final String email;
    private final String userId;
    private final List<GrantedAuthority> grantedAuthorities =
            List.of(new SimpleGrantedAuthority("ROLE_PRE_AUTH"));
    private final Object credentials;
    @Builder.Default private final Step step = Step.DEFAULT;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return email;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}

    @Override
    public String getName() {
        return email;
    }
}
