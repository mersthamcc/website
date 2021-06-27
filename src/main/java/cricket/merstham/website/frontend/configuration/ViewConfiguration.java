package cricket.merstham.website.frontend.configuration;

import cricket.merstham.website.frontend.menu.MenuBuilder;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.DigestUtils;
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
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@ComponentScan("cricket.merstham.website.frontend")
public class ViewConfiguration implements HandlerInterceptor, WebMvcConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(ViewConfiguration.class);

    private final MenuBuilder menuBuilderProvider;
    private final ClubConfiguration clubConfiguration;
    private final String resourcePrefix;

    @Autowired
    public ViewConfiguration(
            MenuBuilder menuBuilderProvider,
            ClubConfiguration clubConfiguration,
            @Value("${resources.base-url}") String resourcePrefix) {
        this.menuBuilderProvider = menuBuilderProvider;
        this.clubConfiguration = clubConfiguration;
        this.resourcePrefix = resourcePrefix;
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
            ModelAndView modelAndView)
            throws Exception {

        if (handler instanceof HandlerMethod && modelAndView != null) {
            Map<String, Object> model = new HashMap<>();
            Principal principal = request.getUserPrincipal();
            if (principal != null) {
                model.put("user", createUserView(principal));
            }
            model.put("config", clubConfiguration);
            model.put("topMenu", menuBuilderProvider.getTopMenu());
            model.put("userMenu", menuBuilderProvider.getUserMenu());
            model.put("mainMenu", menuBuilderProvider.getFrontEndMenu());
            CurrentRoute currentRoute = getCurrentRoute(request, handler);
            model.put("currentRoute", currentRoute);
            model.put("breadcrumbs", menuBuilderProvider.getBreadcrumbs(currentRoute));
            model.put("resourcePrefix", resourcePrefix);
            model.put("dashboardMenu", menuBuilderProvider.getDashboardMenu());
            model.put("adminMenus", new LinkedHashMap<>() {{
                        put("content", menuBuilderProvider.getAdminContentMenu());
                        put("administration", menuBuilderProvider.getAdminAdministrationMenu());
                        put("system", menuBuilderProvider.getAdminSystemMenu());
                    }}
            );

            modelAndView.addAllObjects(model);
        }
    }

    private UserView createUserView(Principal principal) {
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) principal;
        KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) token.getPrincipal();
        List<String> roles =
                ((Collection<SimpleGrantedAuthority>)
                                SecurityContextHolder.getContext()
                                        .getAuthentication()
                                        .getAuthorities())
                        .stream().map(r -> r.getAuthority()).collect(Collectors.toList());
        return new UserView(keycloakPrincipal, roles);
    }

    private CurrentRoute getCurrentRoute(HttpServletRequest request, Object handler) {
        CurrentRoute route = new CurrentRoute();
        Method method = ((HandlerMethod) handler).getMethod();
        route.setMethod(method).setRequestMapping(method.getAnnotation(RequestMapping.class));
        try {
            route.setPathVariables(
                    (LinkedHashMap<String, String>)
                            request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
        } catch (Exception ignored) {

        }
        LOG.info(
                "Matched route = {}, parameters = {}",
                route.getName(),
                String.join(
                        ", ",
                        route.getPathVariables().entrySet().stream()
                                .map(e -> e.getKey() + "=" + e.getValue())
                                .collect(Collectors.toList())));
        return route;
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
            for (var role : roles) {
                if (this.roles.contains(role)) return true;
            }
            return false;
        }

        public String getGravatarHash() throws NoSuchAlgorithmException {
            return DigestUtils.md5DigestAsHex(getEmail().toLowerCase().getBytes()).toLowerCase();
        }
    }

    public class CurrentRoute {

        private Method method;
        private RequestMapping requestMapping;
        private LinkedHashMap<String, String> pathVariables;

        public Method getMethod() {
            return method;
        }

        public CurrentRoute setMethod(Method method) {
            this.method = method;
            return this;
        }

        public RequestMapping getRequestMapping() {
            return requestMapping;
        }

        public CurrentRoute setRequestMapping(RequestMapping requestMapping) {
            this.requestMapping = requestMapping;
            return this;
        }

        public LinkedHashMap<String, String> getPathVariables() {
            return pathVariables;
        }

        public String[] getArgumentValues() {
            return pathVariables.values().toArray(new String[0]);
        }

        public CurrentRoute setPathVariables(Map<String, String> pathVariables) {
            this.pathVariables =
                    pathVariables == null
                            ? new LinkedHashMap<>()
                            : new LinkedHashMap<>(pathVariables);
            return this;
        }

        public String getName() {
            return requestMapping.name();
        }
    }
}
