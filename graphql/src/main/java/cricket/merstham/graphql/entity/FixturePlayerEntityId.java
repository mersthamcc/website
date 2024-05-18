package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class FixturePlayerEntityId implements Serializable {
    @Serial private static final long serialVersionUID = -707536411880377227L;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fixture_id", nullable = false, insertable = false, updatable = false)
    private FixtureEntity fixture;

    @Column(name = "player_id")
    private Integer playerId;
}
