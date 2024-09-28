package cricket.merstham.graphql.services;

import com.google.common.primitives.Bytes;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import static cricket.merstham.graphql.configuration.CacheConfiguration.TOKEN_PKCE_VERIFIER;
import static cricket.merstham.graphql.configuration.CacheConfiguration.TOKEN_STATE;

@Component
public class OAuthHelperService {

    private static final int ENTROPY_BYTES = 32;
    private final SecureRandom random = new SecureRandom();
    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    @Cacheable(value = TOKEN_PKCE_VERIFIER, key = "#name")
    public String getPkceCodeVerifier(String name) {
        return getRandomString(name);
    }

    @Cacheable(value = TOKEN_STATE, key = "#name")
    public String getState(String name) {
        return getRandomString(name);
    }

    @Caching(
            evict = {
                @CacheEvict(value = TOKEN_PKCE_VERIFIER, key = "#name"),
                @CacheEvict(value = TOKEN_STATE, key = "#name")
            })
    public void clear(String name) {}

    public String getPkceCodeChallenge(String verifier) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return encoder.encodeToString(digest.digest(verifier.getBytes(StandardCharsets.UTF_8)));
    }

    private String getRandomString(String base) {
        byte[] buffer = new byte[ENTROPY_BYTES];
        random.nextBytes(buffer);

        return encoder.encodeToString(Bytes.concat(base.getBytes(StandardCharsets.UTF_8), buffer));
    }
}
