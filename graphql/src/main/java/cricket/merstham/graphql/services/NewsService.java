package cricket.merstham.graphql.services;

import cricket.merstham.graphql.entity.NewsEntity;
import cricket.merstham.graphql.repository.NewsEntityRepository;
import cricket.merstham.shared.dto.KeyValuePair;
import cricket.merstham.shared.dto.News;
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

import static cricket.merstham.graphql.configuration.CacheConfiguration.NEWS_ITEM_BY_ID_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.NEWS_ITEM_BY_PATH_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.NEWS_SUMMARY_CACHE;
import static cricket.merstham.graphql.configuration.CacheConfiguration.NEWS_SUMMARY_TOTAL_CACHE;

@Component
public class NewsService {

    private final NewsEntityRepository repository;
    private final ModelMapper mapper;

    @Autowired
    public NewsService(NewsEntityRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Cacheable(value = NEWS_SUMMARY_CACHE, key = "#page")
    public List<News> getNewsFeed(int page) {
        return repository
                .findAll(PageRequest.of(page, 10, Sort.by("publishDate").descending()))
                .map(this::convertToDto)
                .stream()
                .toList();
    }

    @Cacheable(value = NEWS_SUMMARY_TOTAL_CACHE, key = "'totals'")
    public Totals getNewsFeedTotals() {
        var recordCount = repository.count();
        return Totals.builder().totalRecords(recordCount).totalMatching(recordCount).build();
    }

    @Cacheable(value = NEWS_ITEM_BY_ID_CACHE, key = "#id")
    public News getNewsItemById(int id) {
        return convertToDto(repository.findById(id).orElseThrow());
    }

    @Cacheable(value = NEWS_ITEM_BY_PATH_CACHE, key = "#path")
    public News getNewsItemByPath(String path) {
        Example<NewsEntity> example = Example.of(NewsEntity.builder().path(path).build());
        return convertToDto(repository.findOne(example).orElseThrow());
    }

    @PreAuthorize("hasRole('ROLE_NEWS')")
    public List<News> getAdminNewsList(int start, int length, String searchString) {
        return repository.adminSearch(start, length, searchString).stream()
                .map(this::convertToDto)
                .toList();
    }

    @PreAuthorize("hasRole('ROLE_NEWS')")
    @Caching(
            evict = {
                @CacheEvict(value = NEWS_SUMMARY_TOTAL_CACHE, key = "'totals'"),
                @CacheEvict(value = NEWS_ITEM_BY_ID_CACHE, key = "#news.id"),
                @CacheEvict(value = NEWS_ITEM_BY_PATH_CACHE, key = "#news.path"),
                @CacheEvict(value = NEWS_SUMMARY_CACHE, allEntries = true)
            })
    public News save(News news) {
        var entity = repository.findById(news.getId()).orElseGet(() -> new NewsEntity());
        mapper.map(news, entity);
        return convertToDto(repository.saveAndFlush(entity));
    }

    @PreAuthorize("hasRole('ROLE_NEWS')")
    @CacheEvict(value = NEWS_ITEM_BY_ID_CACHE, key = "#id")
    public News saveAttributes(int id, List<KeyValuePair> attributes) {
        var news = repository.findById(id).orElseThrow();

        attributes.forEach(a -> news.getAttributes().put(a.getKey(), a.getValue()));
        return convertToDto(repository.saveAndFlush(news));
    }

    @PreAuthorize("hasRole('ROLE_NEWS')")
    @Caching(
            evict = {
                @CacheEvict(value = NEWS_SUMMARY_TOTAL_CACHE, key = "'totals'"),
                @CacheEvict(value = NEWS_ITEM_BY_ID_CACHE, key = "#news.id"),
                @CacheEvict(value = NEWS_ITEM_BY_PATH_CACHE, key = "#news.path"),
                @CacheEvict(value = NEWS_SUMMARY_CACHE, allEntries = true)
            })
    public News delete(int id) {
        var news = repository.findById(id).orElseThrow();
        repository.delete(news);
        return convertToDto(news);
    }

    private News convertToDto(NewsEntity news) {
        return mapper.map(news, News.class);
    }
}
