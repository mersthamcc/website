package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "club_draw")
public class ClubDrawEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "draw_date", nullable = false)
    private LocalDate drawDate;

    @NotNull
    @Column(name = "prize_fund", nullable = false, precision = 10, scale = 2)
    private BigDecimal prizeFund;

    @OneToMany(mappedBy = "includeInClubDraw")
    private Set<ClubDrawPaymentEntity> clubDrawPayments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "clubDraw")
    private Set<ClubDrawWinnerEntity> clubDrawWinners = new LinkedHashSet<>();
}
