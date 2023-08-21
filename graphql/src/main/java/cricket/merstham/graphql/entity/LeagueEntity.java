package cricket.merstham.graphql.entity;

import com.fasterxml.jackson.databind.JsonNode;
import cricket.merstham.graphql.jpa.JpaJsonbConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "league")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class LeagueEntity {

    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "last_update", nullable = false)
    private Instant lastUpdate;

    @Column(name = "\"table\"", nullable = false)
    @Convert(converter = JpaJsonbConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode table;
}
