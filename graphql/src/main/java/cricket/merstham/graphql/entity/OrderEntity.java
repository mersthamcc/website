package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "\"order\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "uuid", length = 128)
    private String uuid;

    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    @Column(name = "accounting_id", length = 128)
    private String accountingId;

    @Column(name = "owner_user_id", nullable = false, length = 64)
    private String ownerUserId;

    @Column(name = "accounting_error")
    private String accountingError;

    @Column(name = "total")
    private BigDecimal total;

    @Column(name = "discount")
    private BigDecimal discount;

    @OneToMany(mappedBy = "order")
    @OrderBy("date")
    private List<PaymentEntity> payment;

    @OneToMany(mappedBy = "order")
    @OrderBy("addedDate")
    private List<MemberSubscriptionEntity> memberSubscription;

    @Column(name = "confirmed", nullable = false)
    private boolean confirmed;
}
