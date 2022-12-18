package cricket.merstham.graphql.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.beans.Transient;
import java.io.Serial;
import java.io.Serializable;

import static cricket.merstham.graphql.configuration.HibernateConfiguration.ENCRYPTED_JSON_TYPE;

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
    @Type(type = ENCRYPTED_JSON_TYPE)
    private JsonNode detail;

    @Transient
    @JsonProperty("name")
    public String getName() {
        return detail.at("/name").asText();
    }
}
