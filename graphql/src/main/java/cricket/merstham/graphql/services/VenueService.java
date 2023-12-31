package cricket.merstham.graphql.services;

import cricket.merstham.graphql.entity.VenueEntity;
import cricket.merstham.graphql.repository.VenueRepository;
import cricket.merstham.shared.dto.Totals;
import cricket.merstham.shared.dto.Venue;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

import static cricket.merstham.graphql.configuration.CacheConfiguration.VENUES_FOR_MENU_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.VENUE_ITEM_BY_ID_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.VENUE_SUMMARY_TOTAL_CACHE;

@Component
public class VenueService {

    private final VenueRepository repository;
    private final ModelMapper mapper;

    @Autowired
    public VenueService(VenueRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Cacheable(value = VENUES_FOR_MENU_CACHE, key = "'menu'")
    public List<Venue> getVenuesForMenu() {
        return repository.venuesForMenu().stream().map(this::convertToDto).toList();
    }

    @Cacheable(value = VENUE_SUMMARY_TOTAL_CACHE, key = "'totals'")
    public Totals getPageTotals() {
        var recordCount = repository.count();
        return Totals.builder().totalRecords(recordCount).totalMatching(recordCount).build();
    }

    @Cacheable(value = VENUE_ITEM_BY_ID_CACHE, key = "#slug")
    public Venue getEventItemById(String slug) {
        return convertToDto(repository.findById(slug).orElseThrow());
    }

    @PreAuthorize("hasRole('ROLE_VENUES')")
    public List<Venue> getAdminEntryList(int start, int length, String searchString) {
        return repository.adminSearch(start, length, searchString).stream()
                .map(this::convertToDto)
                .toList();
    }

    @PreAuthorize("hasRole('ROLE_VENUES')")
    @Caching(
            evict = {
                @CacheEvict(value = VENUE_SUMMARY_TOTAL_CACHE, key = "'totals'"),
                @CacheEvict(value = VENUES_FOR_MENU_CACHE, key = "'menu'"),
                @CacheEvict(value = VENUE_ITEM_BY_ID_CACHE, key = "#venue.slug")
            })
    public Venue save(Venue venue) {
        var entity = repository.findById(venue.getSlug()).orElseGet(VenueEntity::new);
        mapper.map(venue, entity);
        return convertToDto(repository.saveAndFlush(entity));
    }

    @PreAuthorize("hasRole('ROLE_VENUES')")
    @Caching(
            evict = {
                @CacheEvict(value = VENUE_SUMMARY_TOTAL_CACHE, key = "'totals'"),
                @CacheEvict(value = VENUES_FOR_MENU_CACHE, key = "'menu'"),
                @CacheEvict(value = VENUE_ITEM_BY_ID_CACHE, key = "#slug")
            })
    public Venue delete(String slug) {
        var entity = repository.findById(slug).orElseThrow();
        repository.delete(entity);
        return convertToDto(entity);
    }

    private Venue convertToDto(VenueEntity page) {
        return mapper.map(page, Venue.class);
    }
}
