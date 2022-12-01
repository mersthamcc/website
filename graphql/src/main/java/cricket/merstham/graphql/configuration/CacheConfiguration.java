package cricket.merstham.graphql.configuration;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@EnableRedisRepositories
@EnableCaching
public class CacheConfiguration {

    public static final String MEMBER_SUMMARY_CACHE = "member_summary";
    public static final String NEWS_SUMMARY_CACHE = "news_feed";
    public static final String NEWS_SUMMARY_TOTAL_CACHE = "news_feed_totals";
    public static final String NEWS_ITEM_BY_ID_CACHE = "news_item_by_id";
    public static final String NEWS_ITEM_BY_PATH_CACHE = "news_item_by_path";
    public static final String TEAM_CACHE = "team";
    public static final String ACTIVE_TEAM_CACHE = "active_team";
    public static final String FIXTURE_CACHE = "fixture";

    @Bean
    public RedisCacheManagerBuilderCustomizer customizer() {
        return builder -> builder
                .initialCacheNames(Set.of(
                        MEMBER_SUMMARY_CACHE,
                        NEWS_SUMMARY_CACHE,
                        NEWS_SUMMARY_TOTAL_CACHE,
                        NEWS_ITEM_BY_ID_CACHE,
                        NEWS_ITEM_BY_PATH_CACHE,
                        TEAM_CACHE,
                        ACTIVE_TEAM_CACHE,
                        FIXTURE_CACHE))
                .enableStatistics()
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig());
    }
}
