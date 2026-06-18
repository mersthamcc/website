package cricket.merstham.graphql.services.webhooks;

import com.fasterxml.jackson.databind.JsonNode;
import cricket.merstham.graphql.entity.MemberAttendanceEntity;
import cricket.merstham.graphql.repository.MemberAttendanceEntityRepository;
import cricket.merstham.graphql.repository.MemberEntityRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.isNull;

@Service
public class SignInAppWebhookProcessor implements WebhookProcessor {
    private static final Logger LOG = LogManager.getLogger(SignInAppWebhookProcessor.class);
    private static final String NAME = "signinapp";
    private static final List<String> EVENT_TYPES = List.of("visitor.sign-in");
    public static final String WEBHOOK_SIGNATURE_HEADER = "x-signinapp-webhook-signature";

    private final String webhookSecret;
    private final MemberAttendanceEntityRepository repository;
    private final MemberEntityRepository memberRepository;

    public SignInAppWebhookProcessor(
            @Value("${configuration.webhooks.signinapp.secret}") String webhookSecret,
            MemberAttendanceEntityRepository repository,
            MemberEntityRepository memberRepository) {
        this.webhookSecret = webhookSecret;
        this.repository = repository;
        this.memberRepository = memberRepository;
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
            String header = httpHeaders.getFirst(WEBHOOK_SIGNATURE_HEADER);
            var parts = parseHeader(header);
            var concatenated = format("%s.%s", parts.t, body);
            String computedSignature =
                    new HmacUtils(HmacAlgorithms.HMAC_SHA_256, webhookSecret).hmacHex(concatenated);
            if (!MessageDigest.isEqual(parts.s1.getBytes(), computedSignature.getBytes())) {
                LOG.warn(
                        "Signatures do not match; actual = {}, expected = {}",
                        parts.s1,
                        computedSignature);
            }
            // Temporarily always return true
            return true;
        } catch (Exception ex) {
            LOG.error("Error validating signature", ex);
            return true;
        }
    }

    @Override
    public boolean processWebhook(JsonNode webhook) {
        var event = webhook.get("event").asText();

        if (EVENT_TYPES.contains(event)) {
            var id = getId(webhook);
            var entity = repository.findById(id).orElse(new MemberAttendanceEntity());
            entity.setId(id);
            entity.setEvent("Training");
            var eventAt = webhook.get("event_at").asText();
            try {
                entity.setTime(Instant.parse(eventAt));
            } catch (DateTimeParseException ex) {
                LOG.error("Cannot parse event_at", ex);
                return false;
            }

            var personalFields = webhook.at("/visitor/personal_fields");
            if (personalFields.has("Membership Number")) {
                var memberId = Integer.parseInt(personalFields.get("Membership Number").asText());

                var member = memberRepository.findById(memberId);
                if (member.isEmpty()) {
                    LOG.error("Cannot process webhook, specified member not found");
                    return false;
                }
                entity.setMember(member.get());
            } else {
                var name = webhook.at("/visitor/name").asText();
                entity.setNonMemberName(name);
            }
            repository.save(entity);
        }
        return true;
    }

    private SignatureParts parseHeader(String header) {
        if (isNull(header)) throw new IllegalArgumentException("Header is null");
        var parts =
                Arrays.stream(header.split(","))
                        .map(s -> s.trim().split("="))
                        .filter(s -> s.length == 2)
                        .collect(Collectors.toMap(s -> s[0].trim(), s -> s[1].trim()));
        if (parts.containsKey("t") && parts.containsKey("s1")) {
            return new SignatureParts(parts.get("t"), parts.get("s1"));
        }
        throw new IllegalArgumentException("Invalid header " + header);
    }

    @Getter
    @AllArgsConstructor
    private class SignatureParts {
        private final String t;
        private final String s1;
    }
}
