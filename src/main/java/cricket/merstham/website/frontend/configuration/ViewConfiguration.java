package cricket.merstham.website.frontend.configuration;

import cricket.merstham.website.frontend.menu.MenuBuilder;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@ComponentScan("cricket.merstham.website.frontend")
public class ViewConfiguration implements HandlerInterceptor, WebMvcConfigurer {

    private MenuBuilder menuBuilderProvider;
    private ClubConfiguration clubConfiguration;

    @Autowired
    public ViewConfiguration(MenuBuilder menuBuilderProvider, ClubConfiguration clubConfiguration) {
        this.menuBuilderProvider = menuBuilderProvider;
        this.clubConfiguration = clubConfiguration;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this);
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) throws Exception {

        if (handler instanceof HandlerMethod && modelAndView != null) {
            Map<String, Object> model = new HashMap<>();
            Principal principal = request.getUserPrincipal();
            if (principal!= null) {
                model.put("user", createUserView(principal));
            }
            model.put("config", clubConfiguration);
            model.put("topMenu", menuBuilderProvider.getTopMenu());
            model.put("userMenu", menuBuilderProvider.getUserMenu());
            model.put("mainMenu", menuBuilderProvider.getFrontEndMenu(getMappingName(request, handler)));

            modelAndView.addAllObjects(model);
        }
    }

    private Object createUserView(Principal principal) {
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) principal;
        KeycloakPrincipal keycloakPrincipal=(KeycloakPrincipal)token.getPrincipal();
        List<String> roles = ((Collection<SimpleGrantedAuthority>) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities())
                .stream().map(r -> r.getAuthority()).collect(Collectors.toList());
        return new UserView(keycloakPrincipal, roles);
    }

    private String getMappingName(HttpServletRequest request, Object handler) {
        Method method = ((HandlerMethod) handler).getMethod();
        return method.getAnnotation(RequestMapping.class).name();
    }


    public static class UserView {
        private KeycloakSecurityContext session;
        private List<String> roles;

        public UserView(KeycloakPrincipal keycloakPrincipal, List<String> roles) {
            this.session = keycloakPrincipal.getKeycloakSecurityContext();
            this.roles = roles;
        }

        public String getGivenName() {
            return session.getIdToken().getGivenName();
        }

        public String getFamilyName() {
            return session.getIdToken().getFamilyName();
        }

        public String getEmail() {
            return session.getIdToken().getEmail();
        }

        public String getId() {
            return session.getIdToken().getId();
        }

        public List<String> getRoles() {
            return roles;
        }

        public boolean hasOneOfRoles(List<String> roles) {
            for (var role: roles) {
                if (this.roles.contains(role)) return true;
            }
            return false;
        }
    }
}
