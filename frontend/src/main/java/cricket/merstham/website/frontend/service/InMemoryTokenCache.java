package cricket.merstham.website.frontend.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class InMemoryTokenCache implements TokenCache {

    private volatile String accessToken;
    private volatile Instant expires;
    private final Clock clock;
    private final Object lock = new Object();

    public InMemoryTokenCache(Clock clock) {
        this.clock = clock;
    }

    @Override
    public Optional<String> getAccessToken() {
        synchronized (lock) {
            return (accessToken == null || clock.instant().isAfter(expires))
                    ? Optional.empty()
                    : Optional.of(accessToken);
        }
    }

    @Override
    public TokenCache updateToken(String accessToken, Duration expires) {
        synchronized (lock) {
            this.accessToken = accessToken;
            this.expires = clock.instant().plus(expires);
        }
        return this;
    }

    @Override
    public TokenCache clear() {
        synchronized (lock) {
            this.accessToken = null;
        }
        return this;
    }
}
