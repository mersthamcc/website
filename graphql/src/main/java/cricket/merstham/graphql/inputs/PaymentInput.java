package cricket.merstham.graphql.inputs;

import org.springframework.data.web.ProjectedPayload;

import java.math.BigDecimal;
import java.time.LocalDate;

@ProjectedPayload
public interface PaymentInput {
    String getId();

    LocalDate getDate();

    String getType();

    String getReference();

    BigDecimal getAmount();

    BigDecimal getProcessingFees();

    boolean isCollected();

    boolean isReconciled();
}
