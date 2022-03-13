package cricket.merstham.graphql.entity;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import static cricket.merstham.graphql.config.HibernateConfiguration.JSON_TYPE;

@Entity
@Table(name = "attribute_definition", indexes = {
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
    @Type(type = JSON_TYPE)
    @Basic(fetch = FetchType.EAGER)
    private JsonNode choices;
}