package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "club_draw_payment")
public class ClubDrawPaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Size(max = 64)
    @Column(name = "payment_reference", length = 64)
    private String paymentReference;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(name = "fees_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal feesAmount;

    @Size(max = 256)
    @Column(name = "accounting_id", length = 256)
    private String accountingId;

    @Size(max = 256)
    @Column(name = "fees_accounting_id", length = 256)
    private String feesAccountingId;

    @NotNull
    @Column(name = "status", nullable = false, length = Integer.MAX_VALUE)
    private String status;

    @Column(name = "reconciled")
    private Boolean reconciled;

    @Column(name = "accounting_error", length = Integer.MAX_VALUE)
    private String accountingError;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "include_in_club_draw_id")
    private ClubDrawEntity includeInClubDraw;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_draw_subscription_id", nullable = false)
    private ClubDrawSubscriptionEntity clubDrawSubscription;
}
