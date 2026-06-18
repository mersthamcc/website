package cricket.merstham.graphql.services.webhooks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.net.Webhook;
import cricket.merstham.graphql.entity.MemberAttendanceEntity;
import cricket.merstham.graphql.entity.MemberEntity;
import cricket.merstham.graphql.repository.MemberAttendanceEntityRepository;
import cricket.merstham.graphql.repository.MemberEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Optional;

import static cricket.merstham.graphql.services.webhooks.SignInAppWebhookProcessor.WEBHOOK_SIGNATURE_HEADER;
import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SignInAppWebhookProcessorTest {

    private static final String WEBHOOK_SECRET = "this-is-a-secret";
    private static final String PAYLOAD =
            """
      {
        "site": {
          "id": 68034,
          "name": "Quality Street",
          "type": "standard",
          "timezone": "Europe/London"
        },
        "event": "visitor.sign-in",
        "visitor": {
          "id": 111,
          "name": "John Smith",
          "badge_url": "https://my.example.com/badges/abcd1234.jpg",
          "is_remote": false,
          "photo_url": null,
          "additional": {
            "Role": "U8 (2026)",
            "Category": "Junior",
            "Age Group": "U8"
          },
          "is_returning": true,
          "personal_fields": {
            "Role": "U8 (2026)",
            "name": "John Smith",
            "Category": "Junior",
            "Age Group": "U8",
            "Membership Number": "111"
          },
          "additional_files": [],
          "is_pre_registered": false,
          "returning_visitor_id": 111
        },
        "event_at": "2026-05-01T16:05:00Z",
        "pushed_at": "2026-05-01T16:05:00Z",
        "idempotency_key": "abcd1234"
      }
    """
                    .trim();
    private static final String UNREGISTERED_PAYLOAD =
            """
        {
           "site": {
             "id": 222,
             "name": "Quality Street",
             "type": "standard",
             "timezone": "Europe/London"
           },
           "event": "visitor.sign-in",
           "visitor": {
             "id": 222222,
             "name": "Joan Jones",
             "badge_url": "https://my.example.com/badges/abcd1234.jpg",
             "is_remote": false,
             "photo_url": null,
             "additional": [],
             "is_returning": true,
             "personal_fields": {
               "name": "Joan Jones"
             },
             "additional_files": [],
             "is_pre_registered": false,
             "returning_visitor_id": 222
           },
           "event_at": "2026-05-01T16:05:00Z",
           "pushed_at": "2026-05-01T16:05:00Z",
           "idempotency_key": "abcd1234"
        }
    """
                    .trim();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private MemberAttendanceEntityRepository repository;
    private MemberEntityRepository memberRepository;
    private SignInAppWebhookProcessor processor;

    @BeforeEach
    void setUp() {
        repository = mock(MemberAttendanceEntityRepository.class);
        memberRepository = mock(MemberEntityRepository.class);
        processor = new SignInAppWebhookProcessor(WEBHOOK_SECRET, repository, memberRepository);
    }

    @Test
    void getName() {
        assertThat(processor.getName()).isEqualTo("signinapp");
    }

    @Test
    void getId() throws IOException {
        JsonNode payload = MAPPER.readTree(PAYLOAD);
        var id = processor.getId(payload);
        assertThat(id).isEqualTo("abcd1234");
    }

    @Test
    void isValid() throws NoSuchAlgorithmException, InvalidKeyException {

        HttpHeaders headers = new HttpHeaders();
        var now = Instant.now().toEpochMilli();
        var payload = String.format("%d.%s", now, PAYLOAD);
        var signature = Webhook.Util.computeHmacSha256(WEBHOOK_SECRET, payload);
        headers.add(WEBHOOK_SIGNATURE_HEADER, String.format("t=%d,s1=%s", now, signature));

        var result = processor.isValid(headers, PAYLOAD);
        assertThat(result).isTrue();
    }

    @Test
    void processWebhookForAnExistingMember() throws JsonProcessingException {
        JsonNode payload = MAPPER.readTree(PAYLOAD);
        var member = MemberEntity.builder().id(111).build();
        var expected =
                MemberAttendanceEntity.builder()
                        .id("abcd1234")
                        .member(member)
                        .time(Instant.parse("2026-05-01T16:05:00Z"))
                        .nonMemberName(null)
                        .event("Training")
                        .build();
        when(memberRepository.findById(111)).thenReturn(Optional.of(member));

        var result = processor.processWebhook(payload);
        assertThat(result).isTrue();
        verify(repository, times(1)).findById("abcd1234");
        verify(repository, times(1))
                .save(
                        argThat(
                                arg ->
                                        arg.getId().equals(expected.getId())
                                                && arg.getEvent().equals(expected.getEvent())
                                                && arg.getTime().equals(expected.getTime())
                                                && isNull(arg.getNonMemberName())
                                                && arg.getMember().equals(member)));
    }

    @Test
    void processWebhookForNonMember() throws JsonProcessingException {
        JsonNode payload = MAPPER.readTree(UNREGISTERED_PAYLOAD);
        var expected =
                MemberAttendanceEntity.builder()
                        .id("abcd1234")
                        .time(Instant.parse("2026-05-01T16:05:00Z"))
                        .nonMemberName("Joan Jones")
                        .event("Training")
                        .build();

        var result = processor.processWebhook(payload);
        assertThat(result).isTrue();
        verify(repository, times(1))
                .save(
                        argThat(
                                arg ->
                                        arg.getId().equals(expected.getId())
                                                && arg.getEvent().equals(expected.getEvent())
                                                && arg.getTime().equals(expected.getTime())
                                                && arg.getNonMemberName()
                                                        .equals(expected.getNonMemberName())
                                                && isNull(arg.getMember())));
    }
}
