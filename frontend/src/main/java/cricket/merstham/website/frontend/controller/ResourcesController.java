package cricket.merstham.website.frontend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.view.RedirectView;

import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.LEGACY_RESOURCES;

@Controller
public class ResourcesController {

    private final String resourcePrefix;

    @Autowired
    public ResourcesController(@Value("${resources.base-url}") String resourcePrefix) {
        this.resourcePrefix = resourcePrefix;
    }

    @GetMapping(value = LEGACY_RESOURCES, name = "legacy-resources-redirect")
    public RedirectView redirectToResourceServer(HttpServletRequest request) {
        String resource =
                (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        return redirectTo(resourcePrefix + resource);
    }
}
