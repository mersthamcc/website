package cricket.merstham.website.frontend.configuration.ckfinder;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import java.util.Locale;

import static java.lang.String.format;
import static java.util.Objects.isNull;

@Named
public class CkFinderAuthenticator implements com.cksource.ckfinder.authentication.Authenticator {

    private HttpServletRequest request;

    @Autowired
    public CkFinderAuthenticator(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public boolean authenticate() {
        var principal = request.getUserPrincipal();
        var section = request.getParameter("section");
        if (isNull(principal) || isNull(section)) return false;

        var authorities = ((KeycloakAuthenticationToken) principal).getAuthorities();
        var roleName = format("ROLE_%s", section.toUpperCase(Locale.ROOT));
        return authorities.stream().anyMatch(r -> r.getAuthority().equals(roleName));
    }
}
