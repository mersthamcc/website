package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "fixture_player_summary")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FixturePlayerSummaryEntity {
    @EmbeddedId private FixturePlayerSummaryEntityId id;

    @MapsId("fixtureId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fixture_id", nullable = false)
    private FixtureEntity fixture;

    @MapsId("playerId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private PlayerEntity player;

    @Column(name = "runs")
    private Integer runs;

    @Column(name = "\"out\"")
    private Boolean out;

    @Column(name = "dnb")
    private Boolean dnb;

    @Column(name = "balls")
    private Integer balls;

    @Column(name = "wickets")
    private Integer wickets;

    @Column(name = "overs")
    private BigDecimal overs;

    @Column(name = "maidens")
    private Integer maidens;

    @Column(name = "catches")
    private Integer catches;

    @Column(name = "fours")
    private Integer fours;

    @Column(name = "sixes")
    private Integer sixes;

    @Column(name = "conceded_runs")
    private Integer concededRuns;

    @Column(name = "how_out")
    private String howOut;
}
