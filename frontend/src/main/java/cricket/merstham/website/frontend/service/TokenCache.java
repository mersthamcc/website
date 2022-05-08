package cricket.merstham.website.frontend.service;

import java.time.Duration;
import java.util.Optional;

public interface TokenCache {
    Optional<String> getAccessToken();

    TokenCache updateToken(String accessToken, Duration expires);

    TokenCache clear();
}
