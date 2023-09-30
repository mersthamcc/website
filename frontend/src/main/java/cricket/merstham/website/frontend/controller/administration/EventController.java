package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.shared.dto.Event;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.model.DataTableColumn;
import cricket.merstham.website.frontend.model.datatables.SspRequest;
import cricket.merstham.website.frontend.model.datatables.SspResponse;
import cricket.merstham.website.frontend.model.datatables.SspResponseDataWrapper;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.EventsService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
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
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_EVENT_AJAX_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_EVENT_BASE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_EVENT_DELETE_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_EVENT_EDIT_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_EVENT_NEW_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_EVENT_SAVE_ROUTE;
import static java.util.Objects.isNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller("AdminEventController")
public class EventController extends SspController<Event> {
    private static final Logger LOG = LoggerFactory.getLogger(EventController.class);
    public static final String ADMINISTRATION_EVENT_EDIT = "administration/event/edit";
    public static final String ADMINISTRATION_EVENT_LIST = "administration/event/list";
    public static final String HAS_ROLE_ROLE_EVENT = "hasRole('ROLE_EVENTS')";
    public static final String EVENT = "event";
    public static final String ERRORS = "errors";

    private final EventsService service;
    private final OAuth2AuthorizedClientService clientService;

    @Autowired
    public EventController(EventsService service, OAuth2AuthorizedClientService clientService) {
        this.service = service;
        this.clientService = clientService;
    }

    @GetMapping(value = ADMIN_EVENT_BASE, name = "admin-event-list")
    @PreAuthorize(HAS_ROLE_ROLE_EVENT)
    public ModelAndView list() {
        return new ModelAndView(
                ADMINISTRATION_EVENT_LIST,
                Map.of(
                        "columns",
                        List.of(
                                new DataTableColumn()
                                        .setKey("event.eventDate")
                                        .setFieldName("formattedDate"),
                                new DataTableColumn().setKey("event.title").setFieldName("title"))));
    }

    @GetMapping(value = ADMIN_EVENT_NEW_ROUTE, name = "admin-event-new")
    @PreAuthorize(HAS_ROLE_ROLE_EVENT)
    public ModelAndView newPost(
            HttpServletRequest request, CognitoAuthentication cognitoAuthentication) {
        var flash = RequestContextUtils.getInputFlashMap(request);
        if (isNull(flash) || flash.isEmpty()) {
            var event =
                    Event.builder()
                            .eventDate(Instant.now())
                            .uuid(UUID.randomUUID().toString())
                            .build();
            return new ModelAndView(ADMINISTRATION_EVENT_EDIT, Map.of(EVENT, event));
        } else {
            return new ModelAndView(
                    ADMINISTRATION_EVENT_EDIT,
                    Map.of(
                            EVENT, flash.get(EVENT),
                            ERRORS, flash.get(ERRORS)));
        }
    }

    @GetMapping(value = ADMIN_EVENT_EDIT_ROUTE, name = "admin-event-edit")
    @PreAuthorize(HAS_ROLE_ROLE_EVENT)
    public ModelAndView editPost(
            CognitoAuthentication cognitoAuthentication, @PathVariable("id") int id)
            throws IOException {
        Event event = service.get(cognitoAuthentication.getOAuth2AccessToken(), id);
        return new ModelAndView(ADMINISTRATION_EVENT_EDIT, Map.of(EVENT, event));
    }

    @GetMapping(value = ADMIN_EVENT_DELETE_ROUTE, name = "admin-event-delete")
    @PreAuthorize(HAS_ROLE_ROLE_EVENT)
    public RedirectView deletePost(
            CognitoAuthentication cognitoAuthentication, @PathVariable("id") int id)
            throws IOException {
        service.delete(cognitoAuthentication.getOAuth2AccessToken(), id);
        return new RedirectView(ADMIN_EVENT_BASE);
    }

    @PostMapping(value = ADMIN_EVENT_SAVE_ROUTE, name = "admin-event-save")
    @PreAuthorize(HAS_ROLE_ROLE_EVENT)
    public RedirectView save(
            CognitoAuthentication cognitoAuthentication,
            Event event,
            RedirectAttributes redirectAttributes)
            throws IOException {
        try {
            service.saveEventItem(cognitoAuthentication.getOAuth2AccessToken(), event);
            return new RedirectView(ADMIN_EVENT_BASE);
        } catch (EntitySaveException ex) {
            redirectAttributes.addFlashAttribute(ERRORS, ex.getErrors());
            redirectAttributes.addFlashAttribute(EVENT, event);
            return new RedirectView(ADMIN_EVENT_NEW_ROUTE);
        }
    }

    @Override
    @PostMapping(
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE,
            path = ADMIN_EVENT_AJAX_ROUTE)
    public @ResponseBody SspResponse<SspResponseDataWrapper<Event>> getData(
            CognitoAuthentication cognitoAuthentication, @RequestBody SspRequest request) {
        try {
            var data =
                    service.getItems(
                            cognitoAuthentication.getOAuth2AccessToken(),
                            request.getStart(),
                            request.getLength(),
                            request.getSearch().getValue());
            return SspResponse.<SspResponseDataWrapper<Event>>builder()
                    .draw(request.getDraw())
                    .data(data.getData())
                    .recordsFiltered(data.getRecordsFiltered())
                    .recordsTotal(data.getRecordsTotal())
                    .build();
        } catch (IOException e) {
            LOG.error("Error getting event items from graph service", e);
            return SspResponse.<SspResponseDataWrapper<Event>>builder()
                    .error(Optional.of(List.of(e.getMessage())))
                    .build();
        }
    }
}
