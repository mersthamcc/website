package cricket.merstham.graphql.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @OneToMany(mappedBy = "order")
    @OrderBy("date")
    private List<PaymentEntity> payment = new ArrayList<>();

    @OneToMany(mappedBy = "order")
    @OrderBy("addedDate")
    private List<MemberSubscriptionEntity> subscription = new ArrayList<>();
}