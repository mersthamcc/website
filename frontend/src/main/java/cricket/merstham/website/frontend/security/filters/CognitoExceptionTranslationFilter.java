package cricket.merstham.website.frontend.security.filters;

import cricket.merstham.website.frontend.security.exceptions.CognitoSessionExpiredException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

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
