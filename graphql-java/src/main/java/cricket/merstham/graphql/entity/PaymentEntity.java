package cricket.merstham.graphql.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
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
}