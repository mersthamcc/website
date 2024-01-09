package cricket.merstham.graphql.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@EnableCaching
@Profile("redis-caching")
public class RedisCachingConfiguration {}
