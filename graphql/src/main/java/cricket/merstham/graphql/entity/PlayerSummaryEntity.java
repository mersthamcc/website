package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

/** Mapping for DB view */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "player_summary")
public class PlayerSummaryEntity {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "fixtures_last_year")
    private Long fixturesLastYear;

    @Column(name = "fixtures_this_year")
    private Long fixturesThisYear;

    @Column(name = "earliest_date")
    private LocalDate earliestDate;
}
