package cricket.merstham.graphql.entity;

import com.fasterxml.jackson.databind.JsonNode;
import cricket.merstham.graphql.jpa.JpaJsonbConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "webhook_received")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class WebhookReceivedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "received", nullable = false)
    private Instant receivedDate;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "reference")
    private String reference;

    @Column(name = "headers")
    @Convert(converter = JpaJsonbConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode headers;

    @Column(name = "body")
    @Convert(converter = JpaJsonbConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode body;

    @Column(name = "processed")
    private Boolean processed;
}
