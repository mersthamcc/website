package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

@Getter
@Entity
@Immutable
@Table(name = "all_duck_takers_statistics")
public class AllDuckTakersStatisticEntity {
    @EmbeddedId private AllDuckTakersStatisticEntityId id;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "matches")
    private Long matches;

    @Column(name = "wickets")
    private Long wickets;

    @Column(name = "ducks")
    private Long ducks;

    @Column(name = "golden_ducks")
    private Long goldenDucks;

    @Column(name = "percentage_ducks")
    private BigDecimal percentageDucks;
}
