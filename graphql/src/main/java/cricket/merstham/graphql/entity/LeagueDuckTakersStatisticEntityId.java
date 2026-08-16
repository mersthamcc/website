package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class LeagueDuckTakersStatisticEntityId implements Serializable {
    @Serial private static final long serialVersionUID = -3847469264101675384L;

    @Column(name = "year")
    private Double year;

    @Column(name = "id")
    private Integer id;
}
