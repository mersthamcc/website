package cricket.merstham.website.frontend.security.filters;

import cricket.merstham.website.frontend.security.exceptions.CognitoSessionExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class CognitoExceptionTranslationFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (CognitoSessionExpiredException ex) {
            SecurityContextHolder.clearContext();
            ((HttpServletResponse) response).sendRedirect("/login?error=expired");
        }
    }
}
