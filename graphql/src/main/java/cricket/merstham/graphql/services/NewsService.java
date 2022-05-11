package cricket.merstham.graphql.services;

import cricket.merstham.graphql.entity.NewsAttributeEntity;
import cricket.merstham.graphql.entity.NewsEntity;
import cricket.merstham.graphql.repository.NewsEntityRepository;
import cricket.merstham.shared.dto.News;
import cricket.merstham.shared.dto.NewsAttribute;
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
import java.util.stream.Collectors;

import static cricket.merstham.graphql.config.CacheConfiguration.NEWS_ITEM_BY_ID_CACHE;
import static cricket.merstham.graphql.config.CacheConfiguration.NEWS_ITEM_BY_PATH_CACHE;
import static cricket.merstham.graphql.config.CacheConfiguration.NEWS_SUMMARY_CACHE;
import static cricket.merstham.graphql.config.CacheConfiguration.NEWS_SUMMARY_TOTAL_CACHE;

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
    public List<NewsEntity> getNewsFeed(int page) {
        return repository
                .findAll(PageRequest.of(page, 10, Sort.by("publishDate").descending()))
                .stream()
                .collect(Collectors.toList());
    }

    @Cacheable(value = NEWS_SUMMARY_TOTAL_CACHE)
    public Totals getNewsFeedTotals() {
        var recordCount = repository.count();
        return Totals.builder().totalRecords(recordCount).totalMatching(recordCount).build();
    }

    @Cacheable(value = NEWS_ITEM_BY_ID_CACHE, key = "#id")
    public NewsEntity getNewsItemById(int id) {
        return repository.findById(id).orElseThrow();
    }

    @Cacheable(value = NEWS_ITEM_BY_PATH_CACHE, key = "#path")
    public NewsEntity getNewsItemByPath(String path) {
        Example<NewsEntity> example = Example.of(NewsEntity.builder().path(path).build());
        return repository.findOne(example).orElseThrow();
    }

    @PreAuthorize("hasRole('ROLE_NEWS')")
    public List<NewsEntity> getAdminNewsList(int start, int length, String searchString) {
        return repository.adminSearch(start, length, searchString).stream()
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ROLE_NEWS')")
    @Caching(
            evict = {
                @CacheEvict(value = NEWS_SUMMARY_TOTAL_CACHE),
                @CacheEvict(value = NEWS_ITEM_BY_ID_CACHE, key = "#news.id"),
                @CacheEvict(value = NEWS_ITEM_BY_PATH_CACHE, key = "#news.path"),
                @CacheEvict(value = NEWS_SUMMARY_CACHE, allEntries = true)
            })
    public NewsEntity save(News news) {
        var entity = repository.findById(news.getId()).orElseGet(() -> new NewsEntity());
        mapper.map(news, entity);
        entity = repository.saveAndFlush(entity);
        return entity;
    }

    @PreAuthorize("hasRole('ROLE_NEWS')")
    @CacheEvict(value = NEWS_ITEM_BY_ID_CACHE, key = "#id")
    public NewsEntity saveAttributes(int id, List<NewsAttribute> attributes) {
        var news = repository.findById(id).orElseThrow();
        attributes.forEach(
                a -> {
                    news.getAttributes().stream()
                            .filter(attr -> attr.getName().equalsIgnoreCase(a.getName()))
                            .findFirst()
                            .orElseGet(
                                    () -> {
                                        var newsAttribute =
                                                NewsAttributeEntity.builder()
                                                        .name(a.getName())
                                                        .news(news)
                                                        .build();
                                        news.getAttributes().add(newsAttribute);
                                        return newsAttribute;
                                    })
                            .setValue(a.getValue());
                });
        var saved = repository.saveAndFlush(news);
        return saved;
    }

    @PreAuthorize("hasRole('ROLE_NEWS')")
    @Caching(
            evict = {
                @CacheEvict(value = NEWS_SUMMARY_TOTAL_CACHE),
                @CacheEvict(value = NEWS_ITEM_BY_ID_CACHE, key = "#news.id"),
                @CacheEvict(value = NEWS_ITEM_BY_PATH_CACHE, key = "#news.path"),
                @CacheEvict(value = NEWS_SUMMARY_CACHE, allEntries = true)
            })
    public NewsEntity delete(int id) {
        var news = repository.findById(id).orElseThrow();
        repository.delete(news);
        return news;
    }
}
