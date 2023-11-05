package cricket.merstham.graphql.controllers;

import cricket.merstham.graphql.services.ContactService;
import cricket.merstham.shared.dto.Contact;
import cricket.merstham.shared.dto.ContactCategory;
import cricket.merstham.shared.dto.KeyValuePair;
import cricket.merstham.shared.dto.Totals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class ContactController {

    private final ContactService service;

    @Autowired
    public ContactController(ContactService service) {
        this.service = service;
    }

    @QueryMapping
    public List<Contact> contactFeed(@Argument("page") int page) {
        return service.getContactsFeed(page - 1);
    }

    @QueryMapping
    public List<ContactCategory> contactCategoryFeed() {
        return service.getCategoryFeed();
    }

    @QueryMapping
    public Totals contactTotals(@Argument("searchString") String searchString) {
        return service.getContactFeedTotals();
    }

    @QueryMapping
    public Contact contactItem(@Argument("id") int id) {
        return service.getEventItemById(id);
    }

    @QueryMapping
    public Contact contactItemBySlug(@Argument("path") String slug) {
        return service.getContactItemBySlug(slug);
    }

    @QueryMapping
    public List<Contact> contacts(
            @Argument("start") int start,
            @Argument("length") int length,
            @Argument("searchString") String searchString,
            Principal principal) {
        return service.getAdminEntryList(start, length, searchString);
    }

    @QueryMapping
    public List<ContactCategory> contactCategories(
            @Argument("start") int start,
            @Argument("length") int length,
            @Argument("searchString") String searchString,
            Principal principal) {
        return service.getCategoryAdminEntryList(start, length, searchString);
    }

    @MutationMapping
    public Contact saveContact(@Argument("contact") Contact contact) {
        return service.save(contact);
    }

    @MutationMapping
    public ContactCategory saveContactCategory(@Argument("category") ContactCategory category) {
        return service.saveCategory(category);
    }

    @MutationMapping
    public Contact saveContactMethods(
            @Argument("id") int id, @Argument("methods") List<KeyValuePair> methods) {
        return service.saveMethods(id, methods);
    }

    @MutationMapping
    public Contact deleteContact(@Argument("id") int id) {
        return service.delete(id);
    }
}
