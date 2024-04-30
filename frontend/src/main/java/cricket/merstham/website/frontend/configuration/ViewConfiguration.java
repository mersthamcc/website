package cricket.merstham.website.frontend.configuration;

import cricket.merstham.website.frontend.menu.MenuBuilder;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.templates.PhoneNumberFormatter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import no.api.freemarker.java8.Java8ObjectWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;

@Configuration
public class ViewConfiguration implements HandlerInterceptor, BeanPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ViewConfiguration.class);

    private final MenuBuilder menuBuilderProvider;
    private final ClubConfiguration clubConfiguration;
    private final String resourcePrefix;
    private final String baseUrl;

    private final boolean debug;

    @Autowired
    public ViewConfiguration(
            MenuBuilder menuBuilderProvider,
            ClubConfiguration clubConfiguration,
            @Value("${resources.base-url}") String resourcePrefix,
            @Value("${base-url}") String baseUrl,
            @Value("${debug}") boolean debug) {
        this.menuBuilderProvider = menuBuilderProvider;
        this.clubConfiguration = clubConfiguration;
        this.resourcePrefix = resourcePrefix;
        this.baseUrl = baseUrl;
        this.debug = debug;
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
            var principal = request.getUserPrincipal();
            if (principal != null && principal instanceof CognitoAuthentication) {
                model.put("user", createUserView(principal));
                if (debug) {
                    model.put("accessToken", ((CognitoAuthentication) principal).getAccessToken());
                }
            }
            model.put("debug", debug);
            model.put("config", clubConfiguration);
            model.put("topMenu", menuBuilderProvider.getTopMenu());
            model.put("userMenu", menuBuilderProvider.getUserMenu());
            model.put("mainMenu", menuBuilderProvider.getFrontEndMenu());
            model.put("accountMenu", menuBuilderProvider.getAccountMenu());

            var currentRoute = getCurrentRoute(request, handler);
            model.put("currentRoute", currentRoute);
            model.put("breadcrumbs", menuBuilderProvider.getBreadcrumbs(currentRoute));
            model.put("resourcePrefix", resourcePrefix);
            model.put("baseUrl", baseUrl);
            model.put("dashboardMenu", menuBuilderProvider.getDashboardMenu());
            model.put("banner", menuBuilderProvider.getBannerMessage());

            var adminMenus = new LinkedHashMap<>();
            adminMenus.put("content", menuBuilderProvider.getAdminContentMenu());
            adminMenus.put("administration", menuBuilderProvider.getAdminAdministrationMenu());
            adminMenus.put("system", menuBuilderProvider.getAdminSystemMenu());
            model.put("adminMenus", adminMenus);
            model.put("parsePhoneNumber", new PhoneNumberFormatter());

            modelAndView.addAllObjects(model);
        }
    }

    private UserView createUserView(Principal principal) {
        List<String> roles =
                ((CognitoAuthentication) principal)
                        .getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return new UserView((OidcUser) ((CognitoAuthentication) principal).getPrincipal(), roles);
    }

    private CurrentRoute getCurrentRoute(HttpServletRequest request, Object handler) {
        var route = new CurrentRoute();
        var method = ((HandlerMethod) handler).getMethod();
        route.setMethod(method);
        try {
            route.setPathVariables(
                    (Map<String, String>)
                            request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
        } catch (Exception ex) {
            LOG.warn("No parameters to cast", ex);
        }
        LOG.debug(
                "Matched route = {}, parameters = {}",
                route.getName(),
                String.join(
                        ", ",
                        route.getPathVariables().entrySet().stream()
                                .map(e -> format("{0}={1}", e.getKey(), e.getValue()))
                                .toList()));
        return route;
    }

    public static class UserView {
        private final OidcUser principal;
        private final List<String> roles;

        public UserView(OidcUser principal, List<String> roles) {
            this.principal = principal;
            this.roles = roles;
        }

        public String getGivenName() {
            return principal.getGivenName();
        }

        public String getFamilyName() {
            return principal.getFamilyName();
        }

        public String getEmail() {
            return principal.getEmail();
        }

        public String getId() {
            return principal.getSubject();
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

        public boolean hasRole(String role) {
            return this.roles.contains(role);
        }

        public String getGravatarHash() {
            return DigestUtils.md5DigestAsHex(getEmail().toLowerCase().getBytes()).toLowerCase();
        }
    }

    public class CurrentRoute {

        private Method method;
        private LinkedHashMap<String, String> pathVariables;

        public Method getMethod() {
            return method;
        }

        public CurrentRoute setMethod(Method method) {
            this.method = method;
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
            for (var annotation : method.getAnnotations()) {
                if (annotation instanceof RequestMapping) {
                    return ((RequestMapping) annotation).name();
                } else if (annotation instanceof GetMapping) {
                    return ((GetMapping) annotation).name();
                } else if (annotation instanceof PostMapping) {
                    return ((PostMapping) annotation).name();
                }
            }
            return "no-mapping-attribute";
        }
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        if (bean instanceof FreeMarkerConfigurer) {
            FreeMarkerConfigurer configurer = (FreeMarkerConfigurer) bean;
            configurer
                    .getConfiguration()
                    .setObjectWrapper(
                            new Java8ObjectWrapper(freemarker.template.Configuration.getVersion()));
        }
        return bean;
    }
}
