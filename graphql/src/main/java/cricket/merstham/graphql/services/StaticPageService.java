package cricket.merstham.graphql.services;

import cricket.merstham.graphql.entity.StaticPageEntity;
import cricket.merstham.graphql.repository.StaticPageRepository;
import cricket.merstham.shared.dto.StaticPage;
import cricket.merstham.shared.dto.Totals;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

import static cricket.merstham.graphql.configuration.CacheConfiguration.PAGE_ITEM_BY_ID_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.PAGE_MENU_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.PAGE_SUMMARY_TOTAL_CACHE;

@Component
public class StaticPageService {

    private final StaticPageRepository repository;
    private final ModelMapper mapper;

    @Autowired
    public StaticPageService(StaticPageRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Cacheable(value = PAGE_SUMMARY_TOTAL_CACHE, key = "'totals'")
    public Totals getPageTotals() {
        var recordCount = repository.count();
        return Totals.builder().totalRecords(recordCount).totalMatching(recordCount).build();
    }

    @Cacheable(value = PAGE_ITEM_BY_ID_CACHE, key = "#slug")
    public StaticPage getEventItemById(String slug) {
        return convertToDto(repository.findById(slug).orElseThrow());
    }

    @PreAuthorize("hasRole('ROLE_PAGES')")
    public List<StaticPage> getAdminEntryList(int start, int length, String searchString) {
        return repository.adminSearch(start, length, searchString).stream()
                .map(this::convertToDto)
                .toList();
    }

    @PreAuthorize("hasRole('ROLE_PAGES')")
    @Caching(
            evict = {
                @CacheEvict(value = PAGE_SUMMARY_TOTAL_CACHE, key = "'totals'"),
                @CacheEvict(value = PAGE_ITEM_BY_ID_CACHE, key = "#page.slug"),
                @CacheEvict(value = PAGE_MENU_CACHE, allEntries = true)
            })
    public StaticPage save(StaticPage page) {
        var entity = repository.findById(page.getSlug()).orElseGet(StaticPageEntity::new);
        mapper.map(page, entity);
        return convertToDto(repository.saveAndFlush(entity));
    }

    @PreAuthorize("hasRole('ROLE_PAGES')")
    @Caching(
            evict = {
                @CacheEvict(value = PAGE_SUMMARY_TOTAL_CACHE, key = "'totals'"),
                @CacheEvict(value = PAGE_ITEM_BY_ID_CACHE, key = "#slug"),
                @CacheEvict(value = PAGE_MENU_CACHE, allEntries = true)
            })
    public StaticPage delete(String slug) {
        var entity = repository.findById(slug).orElseThrow();
        repository.delete(entity);
        return convertToDto(entity);
    }

    @Cacheable(value = PAGE_MENU_CACHE)
    public List<StaticPage> pagesForMenus() {
        return repository.findAllByMenuIsNotNull().stream().map(this::convertToDto).toList();
    }

    private StaticPage convertToDto(StaticPageEntity page) {
        return mapper.map(page, StaticPage.class);
    }
}
