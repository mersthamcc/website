package cricket.merstham.graphql.entity;

import com.fasterxml.jackson.databind.JsonNode;
import cricket.merstham.graphql.jpa.JpaJsonbConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
    private TeamEntity team;

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
    @Convert(converter = JpaJsonbConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode detail;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "primaryKey.fixture",
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    private List<FixturePlayerEntity> players;

    @ColumnDefault("false")
    @Column(name = "include_in_fantasy")
    private Boolean includeInFantasy;

    @Column(name = "calendar_id")
    private String calendarId;

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
