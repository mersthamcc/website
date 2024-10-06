package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.HashMap;

import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;
import static java.text.MessageFormat.format;

@Controller("adminSystemController")
public class SystemController {

    private static final String HAS_SYSTEM_ROLE = "hasRole('ROLE_SYSTEM')";

    private final SystemService service;
    private final String baseUrl;

    @Autowired
    public SystemController(SystemService service, @Value("${base-url}") String baseUrl) {
        this.service = service;
        this.baseUrl = baseUrl;
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
