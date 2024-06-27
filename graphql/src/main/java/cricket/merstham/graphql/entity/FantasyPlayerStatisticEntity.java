package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

/** Mapping for DB view */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "fantasy_player_statistics")
public class FantasyPlayerStatisticEntity {
    @EmbeddedId private FantasyPlayerStatisticEntityId id;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "matches")
    private Long matches;

    @Column(name = "runs")
    private Long runs;

    @Column(name = "wickets")
    private Long wickets;

    @Column(name = "catches")
    private Long catches;

    @Column(name = "maidens")
    private Long maidens;

    @Column(name = "fifties")
    private Long fifties;

    @Column(name = "hundreds")
    private Long hundreds;

    @Column(name = "ducks")
    private Long ducks;

    @Column(name = "conceded_runs")
    private Long concededRuns;

    @Column(name = "overs")
    private BigDecimal overs;

    @Column(name = "not_out")
    private Long notOut;
}
