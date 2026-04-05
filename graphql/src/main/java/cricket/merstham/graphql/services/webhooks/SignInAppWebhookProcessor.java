package cricket.merstham.graphql.services.webhooks;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SignInAppWebhookProcessor implements WebhookProcessor {
    private static final Logger LOG = LogManager.getLogger(SignInAppWebhookProcessor.class);
    private static final String NAME = "signinapp";
    public static final String WEBHOOK_SIGNATURE_HEADER = "x-signinapp-webhook-signature";

    private final String webhookSecret;

    @Autowired
    public SignInAppWebhookProcessor(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getId(JsonNode webhook) {
        return webhook.get("idempotency_key").asText();
    }

    @Override
    public boolean isValid(HttpHeaders httpHeaders, String body) {
        try {
            return checkSignature(httpHeaders, body);
        } catch (Exception ex) {
            LOG.warn("Signature validation failed on SignInApp webhook, rejecting", ex);
            return false;
        }
    }

    @Override
    public boolean processWebhook(JsonNode webhook) {
        return true;
    }

    private boolean checkSignature(HttpHeaders httpHeaders, String body) {
        var header = parseHeaders(httpHeaders);

        String signedPayload = String.format("%s.%s", header.get("t"), body);

        String computedSignature =
                new HmacUtils(HmacAlgorithms.HMAC_SHA_256, webhookSecret).hmacHex(signedPayload);
        return MessageDigest.isEqual(header.get("s1").getBytes(), computedSignature.getBytes());
    }

    private Map<String, String> parseHeaders(HttpHeaders httpHeaders) {
        var header = httpHeaders.getFirst(WEBHOOK_SIGNATURE_HEADER);

        return Arrays.stream(header.split(","))
                .map(t -> t.split("="))
                .collect(Collectors.toMap(strings -> strings[0], strings -> strings[1]));
    }
}
