package cricket.merstham.graphql.services;

import cricket.merstham.graphql.entity.EventEntity;
import cricket.merstham.graphql.repository.EventEntityRepository;
import cricket.merstham.shared.dto.Event;
import cricket.merstham.shared.dto.KeyValuePair;
import cricket.merstham.shared.dto.Totals;
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

import static cricket.merstham.graphql.configuration.CacheConfiguration.EVENT_ITEM_BY_ID_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.EVENT_ITEM_BY_PATH_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.EVENT_SUMMARY_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.EVENT_SUMMARY_TOTAL_CACHE;

@Component
public class EventsService {

    private final EventEntityRepository repository;
    private final ModelMapper mapper;

    @Autowired
    public EventsService(EventEntityRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Cacheable(value = EVENT_SUMMARY_CACHE, key = "#page")
    public List<Event> getEventsFeed(int page) {
        return repository
                .findAll(PageRequest.of(page, 10, Sort.by("eventDate").descending()))
                .map(this::convertToDto)
                .stream()
                .toList();
    }

    @Cacheable(value = EVENT_SUMMARY_TOTAL_CACHE)
    public Totals getEventFeedTotals() {
        var recordCount = repository.count();
        return Totals.builder().totalRecords(recordCount).totalMatching(recordCount).build();
    }

    @Cacheable(value = EVENT_ITEM_BY_ID_CACHE, key = "#id")
    public Event getEventItemById(int id) {
        return convertToDto(repository.findById(id).orElseThrow());
    }

    @Cacheable(value = EVENT_ITEM_BY_PATH_CACHE, key = "#path")
    public Event getEventItemByPath(String path) {
        Example<EventEntity> example = Example.of(EventEntity.builder().path(path).build());
        return convertToDto(repository.findOne(example).orElseThrow());
    }

    @PreAuthorize("hasRole('ROLE_EVENTS')")
    public List<Event> getAdminEntryList(int start, int length, String searchString) {
        return repository.adminSearch(start, length, searchString).stream()
                .map(this::convertToDto)
                .toList();
    }

    @PreAuthorize("hasRole('ROLE_EVENTS')")
    @Caching(
            evict = {
                @CacheEvict(value = EVENT_SUMMARY_TOTAL_CACHE),
                @CacheEvict(value = EVENT_ITEM_BY_ID_CACHE, key = "#event.id"),
                @CacheEvict(value = EVENT_ITEM_BY_PATH_CACHE, key = "#event.path", condition = "#event.path != null"),
                @CacheEvict(value = EVENT_SUMMARY_CACHE, allEntries = true)
            })
    public Event save(Event event) {
        var entity = repository.findById(event.getId()).orElseGet(EventEntity::new);
        mapper.map(event, entity);
        return convertToDto(repository.saveAndFlush(entity));
    }

    @PreAuthorize("hasRole('ROLE_EVENTS')")
    @CacheEvict(value = EVENT_ITEM_BY_ID_CACHE, key = "#id")
    public Event saveAttributes(int id, List<KeyValuePair> attributes) {
        var entity = repository.findById(id).orElseThrow();

        attributes.forEach(a -> entity.getAttributes().put(a.getKey(), a.getValue()));
        return convertToDto(repository.saveAndFlush(entity));
    }

    @PreAuthorize("hasRole('ROLE_EVENTS')")
    @Caching(
            evict = {
                @CacheEvict(value = EVENT_SUMMARY_TOTAL_CACHE),
                @CacheEvict(value = EVENT_ITEM_BY_ID_CACHE, key = "#news.id"),
                @CacheEvict(value = EVENT_ITEM_BY_PATH_CACHE, key = "#news.path"),
                @CacheEvict(value = EVENT_SUMMARY_CACHE, allEntries = true)
            })
    public Event delete(int id) {
        var event = repository.findById(id).orElseThrow();
        repository.delete(event);
        return convertToDto(event);
    }

    private Event convertToDto(EventEntity event) {
        return mapper.map(event, Event.class);
    }
}
