package cricket.merstham.website.frontend.configuration;

import cricket.merstham.website.frontend.service.InMemoryTokenCache;
import cricket.merstham.website.frontend.service.TokenCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TokenCacheConfiguration {

    @Bean
    public TokenCache createTokenManager() {
        return new InMemoryTokenCache(Clock.systemUTC());
    }
}
