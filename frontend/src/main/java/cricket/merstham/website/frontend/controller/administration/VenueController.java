package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.shared.dto.Venue;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.model.DataTableColumn;
import cricket.merstham.website.frontend.model.datatables.SspRequest;
import cricket.merstham.website.frontend.model.datatables.SspResponse;
import cricket.merstham.website.frontend.model.datatables.SspResponseDataWrapper;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.VenueService;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cricket.merstham.website.frontend.helpers.RedirectHelper.redirectTo;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_VENUE_AJAX_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_VENUE_BASE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_VENUE_DELETE_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_VENUE_EDIT_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_VENUE_NEW_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_VENUE_SAVE_ROUTE;
import static java.util.Objects.isNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller("adminVenueController")
public class VenueController extends SspController<Venue> {
    private static final Logger LOG = LoggerFactory.getLogger(VenueController.class);
    public static final String ADMINISTRATION_VENUE_EDIT = "administration/venue/edit";
    public static final String ADMINISTRATION_VENUE_LIST = "administration/venue/list";
    public static final String HAS_ROLE_ROLE_VENUES = "hasRole('ROLE_VENUES')";
    public static final String VENUE = "venue";
    public static final String ERRORS = "errors";

    private final VenueService service;

    @Autowired
    public VenueController(VenueService service) {
        this.service = service;
    }

    @GetMapping(value = ADMIN_VENUE_BASE, name = "admin-venue-list")
    @PreAuthorize(HAS_ROLE_ROLE_VENUES)
    public ModelAndView list() {
        return new ModelAndView(
                ADMINISTRATION_VENUE_LIST,
                Map.of(
                        "columns",
                        List.of(
                                new DataTableColumn().setKey("venue.slug").setFieldName("slug"),
                                new DataTableColumn().setKey("venue.name").setFieldName("name"))));
    }

    @GetMapping(value = ADMIN_VENUE_NEW_ROUTE, name = "admin-venue-new")
    @PreAuthorize(HAS_ROLE_ROLE_VENUES)
    public ModelAndView create(HttpServletRequest request) {
        var flash = RequestContextUtils.getInputFlashMap(request);
        if (isNull(flash) || flash.isEmpty()) {
            var venue = Venue.builder().build();
            return new ModelAndView(ADMINISTRATION_VENUE_EDIT, Map.of(VENUE, venue));
        } else {
            Venue venue = (Venue) flash.get(VENUE);
            return new ModelAndView(
                    ADMINISTRATION_VENUE_EDIT, Map.of(VENUE, venue, ERRORS, flash.get(ERRORS)));
        }
    }

    @GetMapping(value = ADMIN_VENUE_EDIT_ROUTE, name = "admin-venue-edit")
    @PreAuthorize(HAS_ROLE_ROLE_VENUES)
    public ModelAndView edit(
            CognitoAuthentication cognitoAuthentication, @PathVariable("slug") String slug)
            throws IOException {
        var contact = service.get(cognitoAuthentication.getOAuth2AccessToken(), slug);
        return new ModelAndView(ADMINISTRATION_VENUE_EDIT, Map.of(VENUE, contact));
    }

    @GetMapping(value = ADMIN_VENUE_DELETE_ROUTE, name = "admin-venue-delete")
    @PreAuthorize(HAS_ROLE_ROLE_VENUES)
    public RedirectView delete(
            CognitoAuthentication cognitoAuthentication, @PathVariable("slug") String slug)
            throws IOException {
        service.delete(cognitoAuthentication.getOAuth2AccessToken(), slug);
        return redirectTo(ADMIN_VENUE_BASE);
    }

    @PostMapping(value = ADMIN_VENUE_SAVE_ROUTE, name = "admin-venue-save")
    @PreAuthorize(HAS_ROLE_ROLE_VENUES)
    public RedirectView save(
            CognitoAuthentication cognitoAuthentication,
            Venue venue,
            RedirectAttributes redirectAttributes)
            throws IOException {
        try {
            service.saveItem(cognitoAuthentication.getOAuth2AccessToken(), venue);
            return redirectTo(ADMIN_VENUE_BASE);
        } catch (EntitySaveException ex) {
            redirectAttributes.addFlashAttribute(ERRORS, ex.getErrors());
            redirectAttributes.addFlashAttribute(VENUE, venue);
            return redirectTo(ADMIN_VENUE_NEW_ROUTE);
        }
    }

    @Override
    @PostMapping(
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE,
            path = ADMIN_VENUE_AJAX_ROUTE)
    public @ResponseBody SspResponse<SspResponseDataWrapper<Venue>> getData(
            CognitoAuthentication cognitoAuthentication, @RequestBody SspRequest request) {
        try {
            var data =
                    service.getItems(
                            cognitoAuthentication.getOAuth2AccessToken(),
                            request.getStart(),
                            request.getLength(),
                            request.getSearch().getValue());
            return SspResponse.<SspResponseDataWrapper<Venue>>builder()
                    .draw(request.getDraw())
                    .data(data.getData())
                    .recordsFiltered(data.getRecordsFiltered())
                    .recordsTotal(data.getRecordsTotal())
                    .build();
        } catch (IOException e) {
            LOG.error("Error getting contact items from graph service", e);
            return SspResponse.<SspResponseDataWrapper<Venue>>builder()
                    .error(Optional.of(List.of(e.getMessage())))
                    .build();
        }
    }
}
