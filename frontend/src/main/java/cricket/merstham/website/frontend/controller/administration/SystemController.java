package cricket.merstham.website.frontend.controller.administration;

import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import cricket.merstham.shared.dto.CalendarSyncResult;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.GraphService;
import cricket.merstham.website.frontend.service.SystemService;
import cricket.merstham.website.graph.system.CalendarSyncMutation;
import cricket.merstham.website.graph.system.ConfigQuery;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;
import static java.text.MessageFormat.format;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller("adminSystemController")
public class SystemController {

    private static final String HAS_SYSTEM_ROLE = "hasRole('ROLE_SYSTEM')";
    private static final Logger LOG = LoggerFactory.getLogger(SystemController.class);

    private final SystemService service;
    private final String baseUrl;
    private final Environment environment;
    private final GraphService graphService;
    private final ModelMapper modelMapper;

    @Autowired
    public SystemController(
            SystemService service,
            @Value("${base-url}") String baseUrl,
            Environment environment,
            GraphService graphService,
            ModelMapper modelMapper) {
        this.service = service;
        this.baseUrl = baseUrl;
        this.environment = environment;
        this.graphService = graphService;
        this.modelMapper = modelMapper;
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

    @GetMapping(value = "/administration/system/sync/calendar", name = "admin-sync-calendar")
    @PreAuthorize(HAS_SYSTEM_ROLE)
    public ModelAndView syncCalendar(CognitoAuthentication cognitoAuthentication) {
        return new ModelAndView("administration/system/sync-calendar");
    }

    @PostMapping(
            consumes = APPLICATION_FORM_URLENCODED_VALUE,
            produces = APPLICATION_JSON_VALUE,
            path = "/administration/system/sync/calendar",
            name = "admin-system-sync-process")
    @PreAuthorize(HAS_SYSTEM_ROLE)
    public @ResponseBody List<CalendarSyncResult> syncCalendarProcess(
            @RequestParam LocalDate startDate, CognitoAuthentication cognitoAuthentication) {
        var syncRequest = CalendarSyncMutation.builder().start(startDate).build();
        Response<CalendarSyncMutation.Data> result =
                graphService.executeMutation(
                        syncRequest, cognitoAuthentication.getOAuth2AccessToken());
        if (result.hasErrors()) {
            result.getErrors().forEach(e -> LOG.error(e.getMessage()));
            throw new EntitySaveException(
                    "Error syncing system calendar",
                    result.getErrors().stream().map(Error::getMessage).toList());
        }
        return result.getData().getCalendarSync().stream()
                .map(s -> modelMapper.map(s, CalendarSyncResult.class))
                .toList();
    }
}
