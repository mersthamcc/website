package cricket.merstham.graphql.services;

import cricket.merstham.graphql.entity.ContactCategoryEntity;
import cricket.merstham.graphql.entity.ContactEntity;
import cricket.merstham.graphql.repository.ContactCategoryEntityRepository;
import cricket.merstham.graphql.repository.ContactEntityRepository;
import cricket.merstham.shared.dto.Contact;
import cricket.merstham.shared.dto.ContactCategory;
import cricket.merstham.shared.dto.KeyValuePair;
import cricket.merstham.shared.dto.Totals;
import cricket.merstham.shared.extensions.StringExtensions;
import lombok.experimental.ExtensionMethod;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

import static cricket.merstham.graphql.configuration.CacheConfiguration.CONTACT_CATEGORY_SUMMARY_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.CONTACT_CATEGORY_SUMMARY_TOTAL_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.CONTACT_ITEM_BY_ID_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.CONTACT_ITEM_BY_PATH_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.CONTACT_SUMMARY_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.CONTACT_SUMMARY_TOTAL_CACHE;
import static java.util.Objects.isNull;

@Component
@ExtensionMethod({StringExtensions.class})
public class ContactService {

    private final ContactEntityRepository repository;
    private final ContactCategoryEntityRepository categoryRepository;
    private final ModelMapper mapper;

    @Autowired
    public ContactService(
            ContactEntityRepository repository,
            ContactCategoryEntityRepository categoryRepository,
            ModelMapper mapper) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Cacheable(value = CONTACT_SUMMARY_CACHE, key = "#page")
    public List<Contact> getContactsFeed(int page) {
        return repository
                .findAll(PageRequest.of(page, 10, Sort.by("position").ascending()))
                .map(this::convertToDto)
                .stream()
                .toList();
    }

    @Cacheable(value = CONTACT_CATEGORY_SUMMARY_CACHE, key = "'all'")
    public List<ContactCategory> getCategoryFeed() {
        return categoryRepository.findAll().stream().map(this::convertToCategoryDto).toList();
    }

    @Cacheable(value = CONTACT_SUMMARY_TOTAL_CACHE, key = "'totals'")
    public Totals getContactFeedTotals() {
        var recordCount = repository.count();
        return Totals.builder().totalRecords(recordCount).totalMatching(recordCount).build();
    }

    @Cacheable(value = CONTACT_CATEGORY_SUMMARY_TOTAL_CACHE, key = "'totals'")
    public Totals getContactCategoryFeedTotals() {
        var recordCount = categoryRepository.count();
        return Totals.builder().totalRecords(recordCount).totalMatching(recordCount).build();
    }

    @Cacheable(value = CONTACT_ITEM_BY_ID_CACHE, key = "#id")
    public Contact getEventItemById(int id) {
        return convertToDto(repository.findById(id).orElseThrow());
    }

    @Cacheable(value = CONTACT_ITEM_BY_PATH_CACHE, key = "#slug")
    public Contact getContactItemBySlug(String slug) {
        Example<ContactEntity> example = Example.of(ContactEntity.builder().slug(slug).build());
        return convertToDto(repository.findOne(example).orElseThrow());
    }

    @PreAuthorize("hasRole('ROLE_CONTACT')")
    public List<Contact> getAdminEntryList(int start, int length, String searchString) {
        return repository.adminSearch(start, length, searchString).stream()
                .map(this::convertToDto)
                .toList();
    }

    @PreAuthorize("hasRole('ROLE_CONTACT')")
    public List<ContactCategory> getCategoryAdminEntryList(
            int start, int length, String searchString) {
        return categoryRepository.adminSearch(start, length, searchString).stream()
                .map(this::convertToCategoryDto)
                .toList();
    }

    @PreAuthorize("hasRole('ROLE_CONTACT')")
    @Caching(
            evict = {
                @CacheEvict(value = CONTACT_SUMMARY_TOTAL_CACHE, allEntries = true),
                @CacheEvict(
                        value = CONTACT_ITEM_BY_ID_CACHE,
                        key = "#contact.id",
                        condition = "#contact.id != null"),
                @CacheEvict(
                        value = CONTACT_ITEM_BY_PATH_CACHE,
                        key = "#contact.slug",
                        condition = "#contact.slug != null"),
                @CacheEvict(value = CONTACT_SUMMARY_CACHE, allEntries = true),
                @CacheEvict(value = CONTACT_CATEGORY_SUMMARY_CACHE, allEntries = true)
            })
    public Contact save(Contact contact) {
        var entity =
                isNull(contact.getId())
                        ? new ContactEntity()
                        : repository.findById(contact.getId()).orElseGet(ContactEntity::new);
        mapper.map(contact, entity);
        if (isNull(entity.getCategory().getId())) {
            entity.setCategory(categoryRepository.save(entity.getCategory()));
        }
        return convertToDto(repository.saveAndFlush(entity));
    }

    @PreAuthorize("hasRole('ROLE_CONTACT')")
    @Caching(
            evict = {
                @CacheEvict(value = CONTACT_SUMMARY_TOTAL_CACHE, allEntries = true),
                @CacheEvict(value = CONTACT_SUMMARY_CACHE, allEntries = true),
                @CacheEvict(value = CONTACT_CATEGORY_SUMMARY_CACHE, allEntries = true)
            })
    public ContactCategory saveCategory(ContactCategory category) {
        var entity =
                isNull(category.getId())
                        ? new ContactCategoryEntity()
                        : categoryRepository
                                .findById(category.getId())
                                .orElseGet(ContactCategoryEntity::new);
        mapper.map(category, entity);

        return convertToCategoryDto(categoryRepository.saveAndFlush(entity));
    }

    @PreAuthorize("hasRole('ROLE_CONTACT')")
    @CacheEvict(value = CONTACT_ITEM_BY_ID_CACHE, key = "#id")
    public Contact saveMethods(int id, List<KeyValuePair> methods) {
        var entity = repository.findById(id).orElseThrow();

        methods.forEach(a -> entity.getMethods().put(a.getKey(), a.getValue()));

        return convertToDto(repository.saveAndFlush(entity));
    }

    @PreAuthorize("hasRole('ROLE_CONTACT')")
    @Caching(
            evict = {
                @CacheEvict(value = CONTACT_SUMMARY_TOTAL_CACHE, allEntries = true),
                @CacheEvict(value = CONTACT_ITEM_BY_ID_CACHE, key = "#id"),
                @CacheEvict(value = CONTACT_ITEM_BY_PATH_CACHE, allEntries = true),
                @CacheEvict(value = CONTACT_SUMMARY_CACHE, allEntries = true),
                @CacheEvict(value = CONTACT_CATEGORY_SUMMARY_CACHE, allEntries = true)
            })
    public Contact delete(int id) {
        var event = repository.findById(id).orElseThrow();
        repository.delete(event);
        return convertToDto(event);
    }

    private Contact convertToDto(ContactEntity contact) {
        return mapper.map(contact, Contact.class);
    }

    private ContactCategory convertToCategoryDto(ContactCategoryEntity category) {
        return mapper.map(category, ContactCategory.class);
    }
}
