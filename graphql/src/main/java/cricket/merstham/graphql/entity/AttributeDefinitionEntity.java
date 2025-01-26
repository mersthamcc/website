package cricket.merstham.graphql.entity;

import com.fasterxml.jackson.databind.JsonNode;
import cricket.merstham.graphql.jpa.JpaJsonbConverter;
import cricket.merstham.shared.types.AttributeType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
        name = "attribute_definition",
        indexes = {
            @Index(name = "idx_attribute_definition_key", columnList = "key", unique = true)
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeDefinitionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "key", nullable = false, length = 64)
    private String key;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AttributeType type;

    @Column(name = "choices", columnDefinition = "jsonb")
    @Convert(converter = JpaJsonbConverter.class)
    @Basic(fetch = FetchType.EAGER)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode choices;
}
