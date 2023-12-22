package cricket.merstham.website.frontend.controller.administration;

import cricket.merstham.shared.dto.Contact;
import cricket.merstham.shared.dto.ContactCategory;
import cricket.merstham.shared.dto.KeyValuePair;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.model.DataTableColumn;
import cricket.merstham.website.frontend.model.datatables.SspRequest;
import cricket.merstham.website.frontend.model.datatables.SspResponse;
import cricket.merstham.website.frontend.model.datatables.SspResponseDataWrapper;
import cricket.merstham.website.frontend.security.CognitoAuthentication;
import cricket.merstham.website.frontend.service.ContactService;
import cricket.merstham.website.frontend.service.contact.ContactMethodManager;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_CONTACT_AJAX_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_CONTACT_BASE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_CONTACT_DELETE_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_CONTACT_EDIT_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_CONTACT_NEW_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_CONTACT_SAVE_ROUTE;
import static java.util.Objects.isNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller("AdminContactController")
public class ContactController extends SspController<Contact> {
    private static final Logger LOG = LoggerFactory.getLogger(ContactController.class);
    public static final String ADMINISTRATION_CONTACT_EDIT = "administration/contacts/edit";
    public static final String ADMINISTRATION_CONTACT_LIST = "administration/contacts/list";
    public static final String HAS_ROLE_ROLE_CONTACT = "hasRole('ROLE_CONTACT')";
    public static final String CONTACT = "contact";
    public static final String ERRORS = "errors";
    public static final String METHODS = "methods";
    public static final String CONTACT_METHODS = "contact_methods";
    public static final String CATEGORIES = "categories";

    private final ContactService service;
    private final ContactMethodManager contactMethodManager;
    private final OAuth2AuthorizedClientService clientService;

    @Autowired
    public ContactController(ContactService service, ContactMethodManager contactMethodManager, OAuth2AuthorizedClientService clientService) {
        this.service = service;
        this.contactMethodManager = contactMethodManager;
        this.clientService = clientService;
    }

    @GetMapping(value = ADMIN_CONTACT_BASE, name = "admin-contact-list")
    @PreAuthorize(HAS_ROLE_ROLE_CONTACT)
    public ModelAndView list() {
        return new ModelAndView(
                ADMINISTRATION_CONTACT_LIST,
                Map.of(
                        "columns",
                        List.of(
                                new DataTableColumn()
                                        .setKey("contact.name")
                                        .setFieldName("name"),
                                new DataTableColumn()
                                        .setKey("contact.position")
                                        .setFieldName("position"),
                                new DataTableColumn()
                                        .setKey("contact.category")
                                        .setFieldName("category.title"))));
    }

    @GetMapping(value = ADMIN_CONTACT_NEW_ROUTE, name = "admin-contact-new")
    @PreAuthorize(HAS_ROLE_ROLE_CONTACT)
    public ModelAndView newContact(
            HttpServletRequest request, CognitoAuthentication cognitoAuthentication) throws IOException {
        var flash = RequestContextUtils.getInputFlashMap(request);
        var categoryMap = service.getCategories(cognitoAuthentication.getOAuth2AccessToken())
                .stream()
                .collect(Collectors.toMap(
                        c -> c.getId().toString(),
                        ContactCategory::getTitle));
        if (isNull(flash) || flash.isEmpty()) {
            var contact = Contact.builder().build();
            return new ModelAndView(
                    ADMINISTRATION_CONTACT_EDIT,
                    Map.of(
                            CONTACT, contact,
                            METHODS, contactMethodManager.getAvailableMethods(),
                            CONTACT_METHODS, contact.getAttributeMap(),
                            CATEGORIES, categoryMap));
        } else {
            Contact contact = (Contact) flash.get(CONTACT);
            return new ModelAndView(
                    ADMINISTRATION_CONTACT_EDIT,
                    Map.of(
                            CONTACT, contact,
                            METHODS, contactMethodManager.getAvailableMethods(),
                            CONTACT_METHODS, contact.getAttributeMap(),
                            CATEGORIES, categoryMap,
                            ERRORS, flash.get(ERRORS)));
        }
    }

    @GetMapping(value = ADMIN_CONTACT_EDIT_ROUTE, name = "admin-contact-edit")
    @PreAuthorize(HAS_ROLE_ROLE_CONTACT)
    public ModelAndView editPost(
            CognitoAuthentication cognitoAuthentication, @PathVariable("id") int id)
            throws IOException {
        var contact = service.get(cognitoAuthentication.getOAuth2AccessToken(), id);
        var categoryMap = service.getCategories(cognitoAuthentication.getOAuth2AccessToken())
                .stream()
                .collect(Collectors.toMap(
                        c -> c.getId().toString(),
                        ContactCategory::getTitle));
        return new ModelAndView(
                ADMINISTRATION_CONTACT_EDIT,
                Map.of(
                        CONTACT, contact,
                        METHODS, contactMethodManager.getAvailableMethods(),
                        CONTACT_METHODS, contact.getAttributeMap(),
                        CATEGORIES, categoryMap));
    }

    @GetMapping(value = ADMIN_CONTACT_DELETE_ROUTE, name = "admin-contact-delete")
    @PreAuthorize(HAS_ROLE_ROLE_CONTACT)
    public RedirectView deletePost(
            CognitoAuthentication cognitoAuthentication, @PathVariable("id") int id)
            throws IOException {
        service.delete(cognitoAuthentication.getOAuth2AccessToken(), id);
        return new RedirectView(ADMIN_CONTACT_BASE);
    }

    @PostMapping(value = ADMIN_CONTACT_SAVE_ROUTE, name = "admin-contact-save")
    @PreAuthorize(HAS_ROLE_ROLE_CONTACT)
    public RedirectView save(
            CognitoAuthentication cognitoAuthentication,
            @RequestBody MultiValueMap<String, Object> data,
            RedirectAttributes redirectAttributes)
            throws IOException {
        Contact contact = toContact(data, cognitoAuthentication);
        try {
            service.saveContactItem(cognitoAuthentication.getOAuth2AccessToken(), contact);
            return new RedirectView(ADMIN_CONTACT_BASE);
        } catch (EntitySaveException ex) {
            redirectAttributes.addFlashAttribute(ERRORS, ex.getErrors());
            redirectAttributes.addFlashAttribute(CONTACT, contact);
            return new RedirectView(ADMIN_CONTACT_NEW_ROUTE);
        }
    }

    private Contact toContact(MultiValueMap<String, Object> data, CognitoAuthentication cognitoAuthentication) throws IOException {
        var category = service.getCategories(cognitoAuthentication.getOAuth2AccessToken())
                .stream()
                .filter(contactCategory -> contactCategory
                        .getId()
                        .equals(Integer.parseInt((String) data.getFirst("category"))))
                .findFirst();
        var builder = Contact.builder();
        builder
                .id(Integer.parseInt((String) data.getFirst("id")))
                .name((String)data.getFirst("name"))
                .position((String)data.getFirst("position"))
                .category(category.orElseThrow());

        List<KeyValuePair> methods = new LinkedList<>();
        contactMethodManager.getAvailableMethods()
                .forEach(method -> {
                    if (data.containsKey(method)) {
                        methods.add(
                                KeyValuePair
                                        .builder()
                                        .key(method)
                                        .value((String) data.getFirst(method))
                                        .build());
                    }
                });
        return builder
                .methods(methods)
                .build();
    }

    @Override
    @PostMapping(
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE,
            path = ADMIN_CONTACT_AJAX_ROUTE)
    public @ResponseBody SspResponse<SspResponseDataWrapper<Contact>> getData(
            CognitoAuthentication cognitoAuthentication, @RequestBody SspRequest request) {
        try {
            var data =
                    service.getItems(
                            cognitoAuthentication.getOAuth2AccessToken(),
                            request.getStart(),
                            request.getLength(),
                            request.getSearch().getValue());
            return SspResponse.<SspResponseDataWrapper<Contact>>builder()
                    .draw(request.getDraw())
                    .data(data.getData())
                    .recordsFiltered(data.getRecordsFiltered())
                    .recordsTotal(data.getRecordsTotal())
                    .build();
        } catch (IOException e) {
            LOG.error("Error getting contact items from graph service", e);
            return SspResponse.<SspResponseDataWrapper<Contact>>builder()
                    .error(Optional.of(List.of(e.getMessage())))
                    .build();
        }
    }
}
