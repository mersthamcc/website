package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FixturePlayerSummaryEntityId implements Serializable {
    @Serial private static final long serialVersionUID = 6027053904999062013L;

    @NotNull
    @Column(name = "fixture_id", nullable = false)
    private Integer fixtureId;

    @NotNull
    @Column(name = "player_id", nullable = false)
    private Integer playerId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FixturePlayerSummaryEntityId entity = (FixturePlayerSummaryEntityId) o;
        return Objects.equals(this.fixtureId, entity.fixtureId)
                && Objects.equals(this.playerId, entity.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fixtureId, playerId);
    }
}
