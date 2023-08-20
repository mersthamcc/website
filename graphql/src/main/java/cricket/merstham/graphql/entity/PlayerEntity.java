package cricket.merstham.graphql.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import cricket.merstham.graphql.jpa.JpaEncryptedJsonbConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.beans.Transient;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "player")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class PlayerEntity implements Serializable {
    @Serial private static final long serialVersionUID = -4107115536165498491L;

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "detail")
    @Convert(converter = JpaEncryptedJsonbConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode detail;

    @Transient
    @JsonProperty("name")
    public String getName() {
        return detail.at("/name").asText();
    }
}
