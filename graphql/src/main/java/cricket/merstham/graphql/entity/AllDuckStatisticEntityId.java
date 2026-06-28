package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class AllDuckStatisticEntityId implements Serializable {
    private static final long serialVersionUID = -6862413475111437867L;

    @Column(name = "year")
    private Integer year;

    @Column(name = "id")
    private Integer id;
}
