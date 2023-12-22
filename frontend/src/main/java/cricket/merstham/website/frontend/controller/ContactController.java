package cricket.merstham.website.frontend.controller;

import cricket.merstham.website.frontend.service.ContactService;
import cricket.merstham.website.frontend.service.contact.ContactMethodManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import static cricket.merstham.website.frontend.helpers.RoleHelper.CONTACT;
import static cricket.merstham.website.frontend.helpers.RoleHelper.hasRole;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.CONTACTS_CATEGORY_HOME_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.CONTACTS_HOME_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.CONTACTS_ITEM_ROUTE;

@Controller
public class ContactController {
    private final ContactService service;

    @Autowired
    public ContactController(ContactService service) {
        this.service = service;
    }

    @GetMapping(value = CONTACTS_HOME_ROUTE, name = "contacts")
    public ModelAndView home() throws IOException {
        return home("officers");
    }

    @GetMapping(value = CONTACTS_CATEGORY_HOME_ROUTE, name = "contact-category")
    public ModelAndView home(@PathVariable(name = "category") String categorySlug)
            throws IOException {
        var items = service.feed();
        var category =
                items.getData().stream()
                        .filter(contactCategory -> contactCategory.getSlug().equals(categorySlug))
                        .findFirst()
                        .orElseThrow();
        return new ModelAndView(
                "contacts/home", Map.of("categories", items.getData(), "current", category));
    }

    //    @GetMapping(value = CONTACTS_ITEM_LEGACY_ROUTE, name = "contacts-item-legacy")
    //    public RedirectView legacyRedirect(@PathVariable("id") int id) throws IOException {
    //        return new RedirectView(service.get(id).getSlug().toString());
    //    }

    @GetMapping(value = CONTACTS_ITEM_ROUTE, name = "contact-item")
    public ModelAndView getItem(
            Principal principal,
            @PathVariable(name = "category-slug") String categorySlug,
            @PathVariable String slug)
            throws IOException {
        var item = service.get(slug);
        return new ModelAndView("contacts/item", Map.of("contact", item));
    }

    private boolean isAdmin(Principal principal) {
        return hasRole(principal, CONTACT);
    }
}
