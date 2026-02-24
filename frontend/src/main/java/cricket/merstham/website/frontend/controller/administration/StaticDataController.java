package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.shared.dto.StaticData;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.model.DataTableColumn;
import cricket.merstham.website.frontend.model.datatables.SspRequest;
import cricket.merstham.website.frontend.model.datatables.SspResponse;
import cricket.merstham.website.frontend.model.datatables.SspResponseDataWrapper;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.StaticDataService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_STATIC_DATA_AJAX_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_STATIC_DATA_BASE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_STATIC_DATA_DELETE_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_STATIC_DATA_EDIT_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_STATIC_DATA_NEW_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_STATIC_DATA_SAVE_ROUTE;
import static java.util.Objects.isNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller("adminStaticDataController")
public class StaticDataController extends SspController<StaticData> {
    private static final Logger LOG = LoggerFactory.getLogger(StaticDataController.class);
    public static final String ADMINISTRATION_STATIC_DATA_EDIT =
            "administration/system/static-data/edit";
    public static final String ADMINISTRATION_STATIC_DATA_LIST =
            "administration/system/static-data/list";
    public static final String HAS_ROLE_ROLE_SYSTEM = "hasRole('ROLE_SYSTEM')";
    public static final String DATA = "data";
    public static final String ERRORS = "errors";

    private final StaticDataService service;

    @Autowired
    public StaticDataController(StaticDataService service) {
        this.service = service;
    }

    @GetMapping(value = ADMIN_STATIC_DATA_BASE, name = "admin-system-data-list")
    @PreAuthorize(HAS_ROLE_ROLE_SYSTEM)
    public ModelAndView list() {
        var model = new HashMap<String, Object>();
        model.put(
                "columns",
                List.of(
                        new DataTableColumn().setKey("static-data.path").setFieldName("path"),
                        new DataTableColumn()
                                .setKey("static-data.statusCode")
                                .setFieldName("statusCode"),
                        new DataTableColumn()
                                .setKey("static-data.contentType")
                                .setFieldName("contentType")));
        return new ModelAndView(ADMINISTRATION_STATIC_DATA_LIST, model);
    }

    @GetMapping(value = ADMIN_STATIC_DATA_NEW_ROUTE, name = "admin-system-data-new")
    @PreAuthorize(HAS_ROLE_ROLE_SYSTEM)
    public ModelAndView create(HttpServletRequest request) {
        var flash = RequestContextUtils.getInputFlashMap(request);
        var model = new HashMap<String, Object>();
        if (isNull(flash) || flash.isEmpty()) {
            var data = StaticData.builder().build();
            model.put(DATA, data);
            return new ModelAndView(ADMINISTRATION_STATIC_DATA_EDIT, model);
        } else {
            StaticData data = (StaticData) flash.get(DATA);
            model.put(DATA, data);
            model.put(ERRORS, flash.get(ERRORS));
            return new ModelAndView(ADMINISTRATION_STATIC_DATA_EDIT, model);
        }
    }

    @GetMapping(value = ADMIN_STATIC_DATA_EDIT_ROUTE, name = "admin-system-data-edit")
    @PreAuthorize(HAS_ROLE_ROLE_SYSTEM)
    public ModelAndView edit(CognitoAuthentication cognitoAuthentication, @PathVariable int id)
            throws IOException {
        var data = service.get(cognitoAuthentication.getOAuth2AccessToken(), id);
        return new ModelAndView(ADMINISTRATION_STATIC_DATA_EDIT, Map.of(DATA, data));
    }

    @GetMapping(value = ADMIN_STATIC_DATA_DELETE_ROUTE, name = "admin-system-data-delete")
    @PreAuthorize(HAS_ROLE_ROLE_SYSTEM)
    public RedirectView delete(CognitoAuthentication cognitoAuthentication, @PathVariable int id)
            throws IOException {
        service.delete(cognitoAuthentication.getOAuth2AccessToken(), id);
        return redirectTo(ADMIN_STATIC_DATA_BASE);
    }

    @PostMapping(value = ADMIN_STATIC_DATA_SAVE_ROUTE, name = "admin-system-data-save")
    @PreAuthorize(HAS_ROLE_ROLE_SYSTEM)
    public RedirectView save(
            CognitoAuthentication cognitoAuthentication,
            StaticData data,
            RedirectAttributes redirectAttributes)
            throws IOException {
        try {
            service.saveItem(cognitoAuthentication.getOAuth2AccessToken(), data);
            return redirectTo(ADMIN_STATIC_DATA_BASE);
        } catch (EntitySaveException ex) {
            redirectAttributes.addFlashAttribute(ERRORS, ex.getErrors());
            redirectAttributes.addFlashAttribute(DATA, data);
            return redirectTo(ADMIN_STATIC_DATA_NEW_ROUTE);
        }
    }

    @Override
    @PostMapping(
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE,
            path = ADMIN_STATIC_DATA_AJAX_ROUTE)
    public @ResponseBody SspResponse<SspResponseDataWrapper<StaticData>> getData(
            CognitoAuthentication cognitoAuthentication, @RequestBody SspRequest request) {
        try {
            var data =
                    service.getItems(
                            cognitoAuthentication.getOAuth2AccessToken(),
                            request.getStart(),
                            request.getLength(),
                            request.getSearch().getValue());
            return SspResponse.<SspResponseDataWrapper<StaticData>>builder()
                    .draw(request.getDraw())
                    .data(data.getData())
                    .recordsFiltered(data.getRecordsFiltered())
                    .recordsTotal(data.getRecordsTotal())
                    .build();
        } catch (IOException e) {
            LOG.error("Error getting static data items from graph service", e);
            return SspResponse.<SspResponseDataWrapper<StaticData>>builder()
                    .error(Optional.of(List.of(e.getMessage())))
                    .build();
        }
    }
}
