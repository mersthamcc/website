package cricket.merstham.website.frontend.configuration;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Component;

import static java.util.Arrays.asList;

@Component
public class CacheConfiguration implements CacheManagerCustomizer<ConcurrentMapCacheManager> {

    public static final String MEMBER_SUMMARY_CACHE = "member_summary";

    @Override
    public void customize(ConcurrentMapCacheManager cacheManager) {
        cacheManager.setCacheNames(asList(MEMBER_SUMMARY_CACHE));
    }
}
