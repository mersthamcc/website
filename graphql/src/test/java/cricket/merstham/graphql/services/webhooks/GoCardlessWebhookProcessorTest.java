package cricket.merstham.graphql.services.webhooks;

import cricket.merstham.graphql.repository.PaymentEntityRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GoCardlessWebhookProcessorTest {

    private final PaymentEntityRepository repository = mock(PaymentEntityRepository.class);
    private final GoCardlessWebhookProcessor processor =
            new GoCardlessWebhookProcessor(null, null, false, repository);

    @Test
    void getName() {
        assertThat(processor.getName()).isEqualTo("gocardless");
    }

    @Test
    void convertAmount() {
        assertThat(processor.convertAmount("200")).isEqualTo(BigDecimal.valueOf(2.00).setScale(2));
        assertThat(processor.convertAmount("112")).isEqualTo(BigDecimal.valueOf(1.12).setScale(2));
        assertThat(processor.convertAmount("2349"))
                .isEqualTo(BigDecimal.valueOf(23.49).setScale(2));
        assertThat(processor.convertAmount("-200")).isEqualTo(BigDecimal.valueOf(2.00).setScale(2));
        assertThat(processor.convertAmount("-112")).isEqualTo(BigDecimal.valueOf(1.12).setScale(2));
        assertThat(processor.convertAmount("-2349"))
                .isEqualTo(BigDecimal.valueOf(23.49).setScale(2));
    }
}
