package cricket.merstham.graphql.helpers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashHelper {
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    public static String generateHashOf(String... values) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            return ENCODER.encodeToString(
                    digest.digest(String.join("-", values).getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
