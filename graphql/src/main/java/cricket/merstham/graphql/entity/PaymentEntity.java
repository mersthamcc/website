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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(name = "type", nullable = false, length = 32)
    private String type;

    @Column(name = "reference", nullable = false, length = 256)
    private String reference;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "processing_fees", nullable = false, precision = 10, scale = 2)
    private BigDecimal processingFees;

    @Column(name = "accounting_id", length = 256)
    private String accountingId;

    @Column(name = "fees_accounting_id", length = 256)
    private String feesAccountingId;

    @Column(name = "collected")
    private Boolean collected;

    @Column(name = "reconciled")
    private Boolean reconciled;

    @Column(name = "accounting_error")
    private String accountingError;
}
