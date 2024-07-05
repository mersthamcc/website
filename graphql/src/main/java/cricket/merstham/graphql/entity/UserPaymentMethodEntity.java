package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "user_payment_methods",
        indexes = {@Index(name = "idx_user_payment_methods_user_id", columnList = "user_id")})
public class UserPaymentMethodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 64)
    @NotNull
    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Size(max = 64)
    @NotNull
    @Column(name = "provider", nullable = false, length = 64)
    private String provider;

    @Size(max = 64)
    @NotNull
    @Column(name = "type", nullable = false, length = 64)
    private String type;

    @Column(name = "customer_identifier", length = Integer.MAX_VALUE)
    private String customerIdentifier;

    @Column(name = "method_identifier", length = Integer.MAX_VALUE)
    private String methodIdentifier;

    @NotNull
    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "status", length = Integer.MAX_VALUE)
    private String status;
}
