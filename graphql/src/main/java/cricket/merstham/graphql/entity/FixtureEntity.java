package cricket.merstham.graphql.entity;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.time.LocalDate;
import java.time.LocalTime;

import static cricket.merstham.graphql.configuration.HibernateConfiguration.JSON_TYPE;

@Entity
@Table(name = "fixture")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FixtureEntity {
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id", nullable = false)
    private TeamEntity teamId;

    @Column(name = "opposition_team_id")
    private Integer oppositionTeamId;

    @Column(name = "opposition", nullable = false)
    private String opposition;

    @Column(name = "ground_id")
    private Integer groundId;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "home_away", nullable = false)
    private String homeAway;

    @Column(name = "start")
    private LocalTime start;

    @Column(name = "detail", nullable = false)
    @Type(type = JSON_TYPE)
    private JsonNode detail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FixtureEntity that = (FixtureEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}