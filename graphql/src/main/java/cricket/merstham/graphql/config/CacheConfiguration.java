package cricket.merstham.graphql.config;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.stereotype.Component;

import static java.util.Arrays.asList;

@Component
@EnableRedisRepositories
@EnableCaching
public class CacheConfiguration implements CacheManagerCustomizer<ConcurrentMapCacheManager> {

    public static final String MEMBER_SUMMARY_CACHE = "member_summary";
    public static final String NEWS_SUMMARY_CACHE = "news_feed";
    public static final String NEWS_SUMMARY_TOTAL_CACHE = "news_feed_totals";
    public static final String NEWS_ITEM_BY_ID_CACHE = "news_item_by_id";
    public static final String NEWS_ITEM_BY_PATH_CACHE = "news_item_by_path";

    @Override
    public void customize(ConcurrentMapCacheManager cacheManager) {
        cacheManager.setCacheNames(
                asList(
                        MEMBER_SUMMARY_CACHE,
                        NEWS_SUMMARY_CACHE,
                        NEWS_SUMMARY_TOTAL_CACHE,
                        NEWS_ITEM_BY_ID_CACHE,
                        NEWS_ITEM_BY_PATH_CACHE));
    }
}
