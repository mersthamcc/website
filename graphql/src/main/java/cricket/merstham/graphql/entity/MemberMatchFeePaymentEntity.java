package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "member_match_fee_payment")
public class MemberMatchFeePaymentEntity {
    @Id
    @Size(max = 128)
    @Column(name = "id", nullable = false, length = 128)
    private String id;

    @NotNull
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @NotNull
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull
    @Column(name = "family_discount", nullable = false, precision = 10, scale = 2)
    private BigDecimal familyDiscount;

    @NotNull
    @Column(name = "gross", nullable = false, precision = 10, scale = 2)
    private BigDecimal gross;

    @NotNull
    @Column(name = "fees", nullable = false, precision = 10, scale = 2)
    private BigDecimal fees;

    @NotNull
    @Column(name = "net", nullable = false, precision = 10, scale = 2)
    private BigDecimal net;

    @NotNull
    @Column(name = "payment_description", nullable = false, length = Integer.MAX_VALUE)
    private String paymentDescription;

    @NotNull
    @Column(name = "product", nullable = false, length = Integer.MAX_VALUE)
    private String product;

    @NotNull
    @Column(name = "member_name", nullable = false, length = Integer.MAX_VALUE)
    private String memberName;

    @NotNull
    @Column(name = "payer_name", nullable = false, length = Integer.MAX_VALUE)
    private String payerName;

    @NotNull
    @Column(name = "member_reference", nullable = false, length = Integer.MAX_VALUE)
    private String memberReference;

    @NotNull
    @Column(name = "payout_date", nullable = false)
    private LocalDate payoutDate;

    @Column(name = "link", length = Integer.MAX_VALUE)
    private String link;

    @Size(max = 128)
    @Column(name = "accounting_id", length = 128)
    private String accountingId;

    @Size(max = 128)
    @Column(name = "accounting_payment_id", length = 128)
    private String accountingPaymentId;

    @Size(max = 128)
    @Column(name = "accounting_fees_id", length = 128)
    private String accountingFeesId;

    @Column(name = "accounting_error", length = Integer.MAX_VALUE)
    private String accountingError;
}
