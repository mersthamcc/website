package cricket.merstham.website.frontend.controller.administration;

import com.apollographql.apollo.api.Response;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.GraphService;
import cricket.merstham.website.frontend.service.SystemService;
import cricket.merstham.website.graph.system.ConfigQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;
import static java.text.MessageFormat.format;

@Controller("adminSystemController")
public class SystemController {

    private static final String HAS_SYSTEM_ROLE = "hasRole('ROLE_SYSTEM')";

    private final SystemService service;
    private final String baseUrl;
    private final Environment environment;
    private final GraphService graphService;

    @Autowired
    public SystemController(
            SystemService service,
            @Value("${base-url}") String baseUrl,
            Environment environment,
            GraphService graphService) {
        this.service = service;
        this.baseUrl = baseUrl;
        this.environment = environment;
        this.graphService = graphService;
    }

    @GetMapping(value = "/administration/system/config", name = "admin-configuration-list")
    @PreAuthorize(HAS_SYSTEM_ROLE)
    public ModelAndView showConfig(CognitoAuthentication cognitoAuthentication) throws IOException {
        Map<String, Object> config = new HashMap<>();
        for (PropertySource<?> source : ((AbstractEnvironment) environment).getPropertySources()) {
            if (source instanceof OriginTrackedMapPropertySource sourceMap) {
                var keys = sourceMap.getSource().keySet();
                keys.forEach(s -> config.put(s, environment.getProperty(s)));
            }
        }
        var query = new ConfigQuery();
        Response<ConfigQuery.Data> result =
                graphService.executeQuery(query, cognitoAuthentication.getOAuth2AccessToken());
        Map<String, Object> model = new HashMap<>();
        model.put("profiles", environment.getActiveProfiles());
        model.put("properties", config);
        model.put("env", ((AbstractEnvironment) environment).getSystemEnvironment());
        model.put("graphProfiles", result.getData().getConfig().getProfiles());
        model.put(
                "graphProperties",
                result.getData().getConfig().getProperties().stream()
                        .collect(
                                Collectors.toMap(
                                        ConfigQuery.Property::getKey,
                                        ConfigQuery.Property::getValue)));
        model.put(
                "graphEnvironment",
                result.getData().getConfig().getEnvironment().stream()
                        .collect(
                                Collectors.toMap(
                                        ConfigQuery.Environment::getKey,
                                        ConfigQuery.Environment::getValue)));
        return new ModelAndView("administration/system/config", model);
    }

    @GetMapping(value = "/administration/system/oauth/{name}", name = "admin-system-oauth-connect")
    @PreAuthorize(HAS_SYSTEM_ROLE)
    public RedirectView systemOauthConnect(
            @PathVariable("name") String name, CognitoAuthentication cognitoAuthentication)
            throws IOException {
        var url =
                service.getAuthUrl(
                        name,
                        format("{0}/administration/system/callbacks/{1}", baseUrl, name),
                        cognitoAuthentication.getOAuth2AccessToken());
        return redirectTo(url);
    }

    @GetMapping(
            value = "/administration/system/callbacks/{name}",
            name = "admin-system-oauth-callback")
    @PreAuthorize(HAS_SYSTEM_ROLE)
    public ModelAndView systemOauthConnectCallback(
            @PathVariable("name") String name,
            @Param("state") String state,
            @Param("code") String code,
            CognitoAuthentication cognitoAuthentication)
            throws IOException {
        var result =
                service.putAuthCode(
                        name,
                        state,
                        code,
                        format("{0}/administration/system/callbacks/{1}", baseUrl, name),
                        cognitoAuthentication.getOAuth2AccessToken());

        var model = new HashMap<String, Object>();
        model.put("name", name);
        model.put("result", result);
        model.put("code", code);
        model.put("state", state);
        return new ModelAndView("administration/system/connect-result", model);
    }
}
