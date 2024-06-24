package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class FantasyPlayerStatisticEntityId implements Serializable {
    private static final long serialVersionUID = 3267291576424133286L;

    @Column(name = "year")
    private Double year;

    @Column(name = "id")
    private Integer id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FantasyPlayerStatisticEntityId entity = (FantasyPlayerStatisticEntityId) o;
        return Objects.equals(this.year, entity.year) && Objects.equals(this.id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, id);
    }
}
