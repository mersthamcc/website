package cricket.merstham.website.frontend.service;

import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import cricket.merstham.shared.dto.Contact;
import cricket.merstham.shared.dto.ContactCategory;
import cricket.merstham.website.frontend.exception.EntitySaveException;
import cricket.merstham.website.frontend.exception.ResourceNotFoundException;
import cricket.merstham.website.frontend.model.datatables.SspGraphResponse;
import cricket.merstham.website.frontend.model.datatables.SspResponseDataWrapper;
import cricket.merstham.website.frontend.service.processors.ItemProcessor;
import cricket.merstham.website.graph.contacts.AdminContactsQuery;
import cricket.merstham.website.graph.contacts.DeleteContactMutation;
import cricket.merstham.website.graph.contacts.FeedQuery;
import cricket.merstham.website.graph.contacts.GetContactCategoriesQuery;
import cricket.merstham.website.graph.contacts.GetContactItemByPathQuery;
import cricket.merstham.website.graph.contacts.GetContactItemQuery;
import cricket.merstham.website.graph.contacts.SaveContactMethodsMutation;
import cricket.merstham.website.graph.contacts.SaveContactMutation;
import cricket.merstham.website.graph.type.ContactCategoryInput;
import cricket.merstham.website.graph.type.ContactInput;
import cricket.merstham.website.graph.type.KeyValuePairInput;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_CONTACT_DELETE_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_CONTACT_EDIT_ROUTE;
import static java.util.Objects.isNull;

@Service
public class ContactService {

    private static final Logger LOG = LoggerFactory.getLogger(ContactService.class);
    private final GraphService graphService;
    private final List<ItemProcessor<Contact>> processors;
    private final ModelMapper modelMapper;

    @Autowired
    public ContactService(
            GraphService graphService,
            List<ItemProcessor<Contact>> processors,
            ModelMapper modelMapper) {
        this.graphService = graphService;
        this.processors = processors;
        this.modelMapper = modelMapper;
    }

    public SspGraphResponse<SspResponseDataWrapper<Contact>> getItems(
            OAuth2AccessToken accessToken, int start, int length, String search)
            throws IOException {
        var query = new AdminContactsQuery(start, length, Input.optional(search));
        Response<AdminContactsQuery.Data> result = graphService.executeQuery(query, accessToken);
        var data = result.getData();
        return SspGraphResponse.<SspResponseDataWrapper<Contact>>builder()
                .data(
                        data.getContacts().stream()
                                .map(
                                        n ->
                                                SspResponseDataWrapper.<Contact>builder()
                                                        .data(modelMapper.map(n, Contact.class))
                                                        .editRouteTemplate(
                                                                Optional.of(ADMIN_CONTACT_EDIT_ROUTE))
                                                        .deleteRouteTemplate(
                                                                Optional.of(
                                                                        ADMIN_CONTACT_DELETE_ROUTE))
                                                        .mapFunction(
                                                                item -> Map.of("id", item.getId()))
                                                        .build())
                                .toList())
                .recordsFiltered(data.getContactTotals().getTotalMatching())
                .recordsTotal(data.getContactTotals().getTotalRecords())
                .build();
    }

    public SspGraphResponse<ContactCategory> feed() throws IOException {
        var query = new FeedQuery();
        Response<FeedQuery.Data> result = graphService.executeQuery(query);
        return SspGraphResponse.<ContactCategory>builder()
                .data(
                        result.getData().getContactCategoryFeed().stream()
                                .map(n -> modelMapper.map(n, ContactCategory.class))
                                .toList())
                .build();
    }

    public List<ContactCategory> getCategories(OAuth2AccessToken accessToken) throws IOException {
        var query = new GetContactCategoriesQuery();
        Response<GetContactCategoriesQuery.Data> result = graphService.executeQuery(query, accessToken);

        return result
                .getData()
                .getContactCategoryFeed()
                .stream()
                .map(c -> modelMapper.map(c, ContactCategory.class))
                .toList();
    }

    public Contact saveContactItem(OAuth2AccessToken accessToken, Contact contact)
            throws IOException {
        var validationErrors =
                processors.stream().map(p -> p.preSave(contact)).flatMap(List::stream).toList();
        if (!validationErrors.isEmpty()) {
            throw new EntitySaveException("Error saving Event", validationErrors);
        }

        var input =
                ContactInput.builder()
                        .id(contact.getId())
                        .name(contact.getName())
                        .position(contact.getPosition())
                        .slug(contact.getSlug())
                        .category(ContactCategoryInput
                                .builder()
                                .id(contact.getCategory().getId())
                                .slug(contact.getCategory().getSlug())
                                .title(contact.getCategory().getTitle())
                                .build())
                        .methods(contact
                                .getMethods()
                                .stream()
                                .map(m -> KeyValuePairInput
                                        .builder()
                                        .key(m.getKey())
                                        .value(m.getValue())
                                        .build())
                                .toList())
                        .build();
        var saveRequest = SaveContactMutation.builder().contact(input).build();
        Response<SaveContactMutation.Data> result =
                graphService.executeMutation(saveRequest, accessToken);
        if (result.hasErrors()) {
            result.getErrors().forEach(e -> LOG.error(e.getMessage()));
            throw new EntitySaveException(
                    "Error saving Contact",
                    result.getErrors().stream().map(Error::getMessage).toList());
        }
        contact.setId(result.getData().getSaveContact().getId());

        processors.forEach(p -> p.postSave(contact));

        return modelMapper.map(result.getData().getSaveContact(), Contact.class);
    }

    public Contact saveContactMethods(OAuth2AccessToken accessToken, Contact contact)
            throws IOException {
        var saveAttributesRequest =
                SaveContactMethodsMutation.builder()
                        .id(contact.getId())
                        .methods(
                                contact.getMethods().stream()
                                        .map(
                                                a ->
                                                        KeyValuePairInput.builder()
                                                                .key(a.getKey())
                                                                .value(a.getValue())
                                                                .build())
                                        .toList())
                        .build();
        Response<SaveContactMethodsMutation.Data> attributeResult =
                graphService.executeMutation(saveAttributesRequest, accessToken);
        if (attributeResult.hasErrors() || isNull(attributeResult.getData())) {
            attributeResult.getErrors().forEach(e -> LOG.error(e.getMessage()));
            throw new EntitySaveException(
                    "Error saving Contact item",
                    attributeResult.getErrors().stream().map(Error::getMessage).toList());
        }
        return modelMapper.map(attributeResult.getData().getSaveContactMethods(), Contact.class);
    }

    public Contact get(OAuth2AccessToken accessToken, int id) throws IOException {
        var query = new GetContactItemQuery(id);
        Response<GetContactItemQuery.Data> item;
        if (isNull(accessToken)) {
            item = graphService.executeQuery(query);
        } else {
            item = graphService.executeQuery(query, accessToken);
        }
        if (isNull(item.getData().getContactItem())) throw new ResourceNotFoundException();
        var result = modelMapper.map(item.getData().getContactItem(), Contact.class);
        processors.forEach(p -> p.postOpen(result));
        return result;
    }

    public Contact get(int id) throws IOException {
        return get(null, id);
    }

    public Contact get(String path) throws IOException {
        var query = new GetContactItemByPathQuery(path);
        Response<GetContactItemByPathQuery.Data> item = graphService.executeQuery(query);
        if (isNull(item.getData().getContactItemByPath())) throw new ResourceNotFoundException();
        var result = modelMapper.map(item.getData().getContactItemByPath(), Contact.class);
        processors.forEach(p -> p.postOpen(result));
        return result;
    }

    public boolean delete(OAuth2AccessToken accessToken, int id) throws IOException {
        var query = new DeleteContactMutation(id);
        Response<DeleteContactMutation.Data> result =
                graphService.executeMutation(query, accessToken);
        if (result.hasErrors()) {
            result.getErrors().forEach(e -> LOG.error(e.getMessage()));
        }
        return !result.hasErrors();
    }
}
