package cricket.merstham.website.frontend.configuration;

import cricket.merstham.website.frontend.menu.MenuBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
import java.util.HashMap;
import java.util.Map;

@Configuration
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

        if (handler instanceof HandlerMethod) {
            Map<String, Object> model = new HashMap<>();
            model.put("config", clubConfiguration);
            model.put("topMenu", menuBuilderProvider.getTopMenu());
            model.put("userMenu", menuBuilderProvider.getUserMenu());
            model.put("mainMenu", menuBuilderProvider.getFrontEndMenu(getMappingName(request, handler)));

            modelAndView.addAllObjects(model);
        }
    }

    private String getMappingName(HttpServletRequest request, Object handler) {
        Method method = ((HandlerMethod) handler).getMethod();
        return method.getAnnotation(RequestMapping.class).name();
    }
}
