package cricket.merstham.website.frontend.security.filters;

import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.CognitoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CognitoRefreshFilter extends OncePerRequestFilter {

    private final CognitoService cognitoService;

    @Autowired
    public CognitoRefreshFilter(CognitoService cognitoService) {
        this.cognitoService = cognitoService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();
        if (authentication instanceof CognitoAuthentication cognitoAuthentication
                && !cognitoAuthentication.isAuthenticated()) {
            var refreshedAuthentication = cognitoService.refresh(cognitoAuthentication);
            context.setAuthentication(refreshedAuthentication);
            SecurityContextHolder.setContext(context);
        }
        filterChain.doFilter(request, response);
    }
}
