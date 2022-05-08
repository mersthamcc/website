package cricket.merstham.graphql.inputs;

import org.springframework.data.web.ProjectedPayload;

import java.math.BigDecimal;
import java.time.LocalDate;

@ProjectedPayload
public interface MemberSubscriptionInput {
    LocalDate getAddedDate();

    int getYear();

    BigDecimal getPrice();

    int getPricelistItemId();

    int getOrderId();
}
