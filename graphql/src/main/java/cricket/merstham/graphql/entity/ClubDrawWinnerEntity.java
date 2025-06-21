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
@Table(name = "club_draw_winner")
public class ClubDrawWinnerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_draw_id", nullable = false)
    private ClubDrawEntity clubDraw;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "club_draw_payment_id", nullable = false)
    private ClubDrawPaymentEntity clubDrawPayment;

    @NotNull
    @Column(name = "prize_percent", nullable = false)
    private Integer prizePercent;

    @NotNull
    @Column(name = "prize_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal prizeAmount;

    @NotNull
    @Column(name = "payout_date", nullable = false)
    private LocalDate payoutDate;
}
