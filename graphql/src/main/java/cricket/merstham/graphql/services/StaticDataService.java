package cricket.merstham.graphql.services;

import cricket.merstham.graphql.entity.StaticDataEntity;
import cricket.merstham.graphql.repository.StaticDataEntityRepository;
import cricket.merstham.shared.dto.StaticData;
import cricket.merstham.shared.dto.Totals;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

import static cricket.merstham.graphql.configuration.CacheConfiguration.STATIC_DATA_ITEM_BY_PATH_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.STATIC_DATA_SUMMARY_TOTAL_CACHE;
import static java.util.Objects.nonNull;

@Service
public class StaticDataService {

    private final StaticDataEntityRepository repository;
    private final ModelMapper mapper;

    @Autowired
    public StaticDataService(StaticDataEntityRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Cacheable(value = STATIC_DATA_SUMMARY_TOTAL_CACHE, key = "'totals'")
    public Totals getStaticDataTotals() {
        var recordCount = repository.count();
        return Totals.builder().totalRecords(recordCount).totalMatching(recordCount).build();
    }

    public StaticData getStaticDataById(int id) {
        return convertToDto(repository.findById(id).orElseThrow());
    }

    @Cacheable(value = STATIC_DATA_ITEM_BY_PATH_CACHE, key = "#path")
    public StaticData getStaticDataByPath(String path) {
        return convertToDto(repository.findByPath(path));
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM')")
    public List<StaticData> getAdminEntryList(int start, int length, String searchString) {
        return repository.adminSearch(start, length, searchString).stream()
                .map(this::convertToDto)
                .toList();
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM')")
    @Caching(
            evict = {
                @CacheEvict(value = STATIC_DATA_SUMMARY_TOTAL_CACHE, key = "'totals'"),
                @CacheEvict(value = STATIC_DATA_ITEM_BY_PATH_CACHE, key = "#data.path")
            })
    public StaticData save(StaticData data) {
        StaticDataEntity entity;
        if (nonNull(data.getId())) {
            entity = repository.findById(data.getId()).orElseThrow();
        } else {
            entity = new StaticDataEntity();
        }
        mapper.map(data, entity);
        return convertToDto(repository.saveAndFlush(entity));
    }

    @PreAuthorize("hasRole('ROLE_SYSTEM')")
    @Caching(
            evict = {
                @CacheEvict(value = STATIC_DATA_SUMMARY_TOTAL_CACHE, key = "'totals'"),
                @CacheEvict(value = STATIC_DATA_ITEM_BY_PATH_CACHE, allEntries = true)
            })
    public StaticData delete(int id) {
        var entity = repository.findById(id).orElseThrow();
        repository.delete(entity);
        return convertToDto(entity);
    }

    private StaticData convertToDto(StaticDataEntity page) {
        return mapper.map(page, StaticData.class);
    }
}
