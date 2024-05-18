package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

/** Mapping for DB view */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Immutable
@Entity
@Table(name = "fixture_player")
public class FixturePlayerEntity {
    @EmbeddedId private FixturePlayerEntityId primaryKey;

    @Column(name = "\"position\"")
    private Integer position;

    @Column(name = "name", length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "captain")
    private Boolean captain;

    @Column(name = "wicket_keeper")
    private Boolean wicketKeeper;
}
