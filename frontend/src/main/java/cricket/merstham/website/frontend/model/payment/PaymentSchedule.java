package cricket.merstham.website.frontend.model.payment;

import java.io.Serializable;
import java.math.BigDecimal;

public class PaymentSchedule implements Serializable {

    private static final long serialVersionUID = 20210530210700L;

    private int numberOfPayments;
    private BigDecimal amount;
    private BigDecimal finalAmount;

    public int getNumberOfPayments() {
        return numberOfPayments;
    }

    public PaymentSchedule setNumberOfPayments(int numberOfPayments) {
        this.numberOfPayments = numberOfPayments;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentSchedule setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public PaymentSchedule setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
        return this;
    }
}
