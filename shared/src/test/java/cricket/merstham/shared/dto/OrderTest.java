package cricket.merstham.shared.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    private static Order UNPAID_ORDER =
            Order.builder().total(BigDecimal.valueOf(100.00)).payment(List.of()).build();

    private static Order FULLY_PAID_ORDER =
            Order.builder()
                    .total(BigDecimal.valueOf(100.00))
                    .payment(
                            List.of(
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("complete")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("complete")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("complete")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("complete")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("complete")
                                            .build()))
                    .build();

    private static Order SCHEDULED_ORDER =
            Order.builder()
                    .total(BigDecimal.valueOf(100.00))
                    .payment(
                            List.of(
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("scheduled")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("scheduled")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("scheduled")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("scheduled")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("scheduled")
                                            .build()))
                    .build();

    private static Order PART_PAID_SCHEDULED_ORDER =
            Order.builder()
                    .total(BigDecimal.valueOf(100.00))
                    .payment(
                            List.of(
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("complete")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("complete")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("complete")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("submitted")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("scheduled")
                                            .build()))
                    .build();

    private static Order CANCELLED_ORDER =
            Order.builder()
                    .total(BigDecimal.valueOf(100.00))
                    .payment(
                            List.of(
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("cancelled")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("cancelled")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("cancelled")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("cancelled")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("cancelled")
                                            .build()))
                    .build();

    private static Order CANCELLED_PART_PAID_ORDER =
            Order.builder()
                    .total(BigDecimal.valueOf(100.00))
                    .payment(
                            List.of(
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("complete")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("cancelled")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("cancelled")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("cancelled")
                                            .build(),
                                    Payment.builder()
                                            .amount(BigDecimal.valueOf(20.00))
                                            .status("cancelled")
                                            .build()))
                    .build();

    @Test
    void getWebReference() {
        Order order = Order.builder().id(1).build();

        assertThat(order.getWebReference()).isEqualTo("WEB-000001");

        order = Order.builder().id(11).build();

        assertThat(order.getWebReference()).isEqualTo("WEB-000011");

        order = Order.builder().id(111).build();

        assertThat(order.getWebReference()).isEqualTo("WEB-000111");

        order = Order.builder().id(1111).build();

        assertThat(order.getWebReference()).isEqualTo("WEB-001111");

        order = Order.builder().id(11111).build();

        assertThat(order.getWebReference()).isEqualTo("WEB-011111");

        order = Order.builder().id(111111).build();

        assertThat(order.getWebReference()).isEqualTo("WEB-111111");

        order = Order.builder().id(1111111).build();

        assertThat(order.getWebReference()).isEqualTo("WEB-1111111");
    }

    @Test
    void isFullyPaid() {
        assertThat(FULLY_PAID_ORDER.isFullyPaid()).isTrue();
        assertThat(UNPAID_ORDER.isFullyPaid()).isFalse();
        assertThat(SCHEDULED_ORDER.isFullyPaid()).isFalse();
        assertThat(CANCELLED_ORDER.isFullyPaid()).isFalse();
        assertThat(CANCELLED_PART_PAID_ORDER.isFullyPaid()).isFalse();
        assertThat(PART_PAID_SCHEDULED_ORDER.isFullyPaid()).isFalse();
    }

    @Test
    void getCollected() {
        assertThat(FULLY_PAID_ORDER.getCollected())
                .isEqualTo(FULLY_PAID_ORDER.getTotal().setScale(2));
        assertThat(UNPAID_ORDER.getCollected()).isEqualTo(BigDecimal.ZERO.setScale(2));
        assertThat(SCHEDULED_ORDER.getCollected()).isEqualTo(BigDecimal.ZERO.setScale(2));
        assertThat(CANCELLED_ORDER.getCollected()).isEqualTo(BigDecimal.ZERO.setScale(2));
        assertThat(CANCELLED_PART_PAID_ORDER.getCollected())
                .isEqualTo(BigDecimal.valueOf(20.00).setScale(2));
        assertThat(PART_PAID_SCHEDULED_ORDER.getCollected())
                .isEqualTo(BigDecimal.valueOf(60.00).setScale(2));
    }

    @Test
    void isPartPaid() {
        assertThat(FULLY_PAID_ORDER.isPartPaid()).isFalse();
        assertThat(UNPAID_ORDER.isPartPaid()).isFalse();
        assertThat(SCHEDULED_ORDER.isPartPaid()).isFalse();
        assertThat(CANCELLED_ORDER.isPartPaid()).isFalse();
        assertThat(CANCELLED_PART_PAID_ORDER.isPartPaid()).isTrue();
        assertThat(PART_PAID_SCHEDULED_ORDER.isPartPaid()).isTrue();
    }

    @Test
    void isOutstanding() {
        assertThat(FULLY_PAID_ORDER.isOutstanding()).isFalse();
        assertThat(UNPAID_ORDER.isOutstanding()).isTrue();
        assertThat(SCHEDULED_ORDER.isOutstanding()).isFalse();
        assertThat(CANCELLED_ORDER.isOutstanding()).isTrue();
        assertThat(CANCELLED_PART_PAID_ORDER.isOutstanding()).isFalse();
        assertThat(PART_PAID_SCHEDULED_ORDER.isOutstanding()).isFalse();
    }

    @Test
    void isScheduled() {
        assertThat(FULLY_PAID_ORDER.isScheduled()).isFalse();
        assertThat(UNPAID_ORDER.isScheduled()).isFalse();
        assertThat(SCHEDULED_ORDER.isScheduled()).isTrue();
        assertThat(CANCELLED_ORDER.isScheduled()).isFalse();
        assertThat(CANCELLED_PART_PAID_ORDER.isScheduled()).isFalse();
        assertThat(PART_PAID_SCHEDULED_ORDER.isScheduled()).isTrue();
    }

    @Test
    void isCancelled() {
        assertThat(FULLY_PAID_ORDER.isCancelled()).isFalse();
        assertThat(UNPAID_ORDER.isCancelled()).isFalse();
        assertThat(SCHEDULED_ORDER.isCancelled()).isFalse();
        assertThat(CANCELLED_ORDER.isCancelled()).isTrue();
        assertThat(CANCELLED_PART_PAID_ORDER.isCancelled()).isTrue();
        assertThat(PART_PAID_SCHEDULED_ORDER.isCancelled()).isFalse();
    }

    @Test
    void isSubmitted() {
        assertThat(FULLY_PAID_ORDER.isSubmitted()).isFalse();
        assertThat(UNPAID_ORDER.isSubmitted()).isFalse();
        assertThat(SCHEDULED_ORDER.isSubmitted()).isFalse();
        assertThat(CANCELLED_ORDER.isSubmitted()).isFalse();
        assertThat(CANCELLED_PART_PAID_ORDER.isSubmitted()).isFalse();
        assertThat(PART_PAID_SCHEDULED_ORDER.isSubmitted()).isTrue();
    }

    @Test
    void isActionRequired() {
        assertThat(FULLY_PAID_ORDER.isActionRequired()).isFalse();
        assertThat(UNPAID_ORDER.isActionRequired()).isTrue();
        assertThat(SCHEDULED_ORDER.isActionRequired()).isFalse();
        assertThat(CANCELLED_ORDER.isActionRequired()).isTrue();
        assertThat(CANCELLED_PART_PAID_ORDER.isActionRequired()).isTrue();
        assertThat(PART_PAID_SCHEDULED_ORDER.isActionRequired()).isFalse();
    }

    @Test
    void getOutstanding() {
        assertThat(FULLY_PAID_ORDER.getOutstanding()).isEqualTo(BigDecimal.ZERO.setScale(2));
        assertThat(UNPAID_ORDER.getOutstanding()).isEqualTo(UNPAID_ORDER.getTotal().setScale(2));
        assertThat(SCHEDULED_ORDER.getOutstanding())
                .isEqualTo(SCHEDULED_ORDER.getTotal().setScale(2));
        assertThat(CANCELLED_ORDER.getOutstanding())
                .isEqualTo(CANCELLED_ORDER.getTotal().setScale(2));
        assertThat(CANCELLED_PART_PAID_ORDER.getOutstanding())
                .isEqualTo(BigDecimal.valueOf(80.00).setScale(2));
        assertThat(PART_PAID_SCHEDULED_ORDER.getOutstanding())
                .isEqualTo(BigDecimal.valueOf(40.00).setScale(2));
    }
}
