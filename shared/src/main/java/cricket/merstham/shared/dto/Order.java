package cricket.merstham.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.beans.Transient;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Order implements Serializable {
    @Serial private static final long serialVersionUID = 5110054976015116223L;

    private Integer id;
    private String uuid;
    private LocalDate createDate;
    private String accountingId;
    private String ownerUserId;
    private List<Payment> payment = new ArrayList<>();
    private List<MemberSubscription> memberSubscription = new ArrayList<>();
    private BigDecimal total;
    private BigDecimal discount;
    private boolean confirmed;

    public String getWebReference() {
        return format("WEB-%1$6s", id).replace(' ', '0');
    }

    @Transient
    public boolean isFullyPaid() {
        BigDecimal paymentTotal = getCollected();
        return total.setScale(2).equals(paymentTotal.setScale(2));
    }

    @Transient
    public BigDecimal getCollected() {
        return payment.stream()
                .filter(p -> "complete".equals(p.getStatus()))
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2);
    }

    @Transient
    public boolean isPartPaid() {
        BigDecimal paymentTotal = getCollected();
        return total.setScale(2).compareTo(paymentTotal.setScale(2)) > 0
                && paymentTotal.setScale(2).compareTo(BigDecimal.ZERO.setScale(2)) > 0;
    }

    @Transient
    public boolean isOutstanding() {
        BigDecimal paymentTotal = getCollected();
        return (!isScheduled())
                && total.setScale(2).compareTo(BigDecimal.ZERO.setScale(2)) > 0
                && paymentTotal.setScale(2).compareTo(BigDecimal.ZERO.setScale(2)) == 0;
    }

    @Transient
    public boolean isScheduled() {
        var count = payment.stream().filter(p -> "scheduled".equals(p.getStatus())).count();
        return total.setScale(2).compareTo(BigDecimal.ZERO.setScale(2)) > 0 && count > 0;
    }

    @Transient
    public boolean isCancelled() {
        var count = payment.stream().filter(p -> "cancelled".equals(p.getStatus())).count();
        return total.setScale(2).compareTo(BigDecimal.ZERO.setScale(2)) > 0 && count > 0;
    }

    @Transient
    public boolean isSubmitted() {
        var count = payment.stream().filter(p -> "submitted".equals(p.getStatus())).count();
        return total.setScale(2).compareTo(BigDecimal.ZERO.setScale(2)) > 0 && count > 0;
    }

    @Transient
    public boolean isActionRequired() {
        BigDecimal paymentTotal = getCollected();
        return total.setScale(2).compareTo(BigDecimal.ZERO.setScale(2)) > 0
                && paymentTotal.setScale(2).compareTo(total.setScale(2)) < 0
                && ((!(isScheduled() || isSubmitted())) || isCancelled());
    }

    @Transient
    public BigDecimal getOutstanding() {
        BigDecimal paymentTotal = getCollected();
        return total.setScale(2).subtract(paymentTotal.setScale(2)).setScale(2);
    }
}
