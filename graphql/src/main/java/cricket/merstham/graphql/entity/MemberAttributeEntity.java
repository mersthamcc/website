package cricket.merstham.graphql.entity;

import com.fasterxml.jackson.databind.JsonNode;
import cricket.merstham.graphql.jpa.JpaEncryptedJsonbConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(
        name = "member_attribute",
        indexes = {
            @Index(name = "idx_member_attribute_member_id", columnList = "member_id"),
            @Index(name = "idx_member_attribute_attribute_id", columnList = "attribute_id")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberAttributeEntity implements Serializable {
    private static final long serialVersionUID = 3171795625796914171L;

    @EmbeddedId private MemberAttributeEntityId primaryKey;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "updated_date", nullable = false)
    private Instant updatedDate;

    @Column(name = "value")
    @Convert(converter = JpaEncryptedJsonbConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode value;

    @Transient
    public MemberEntity getMember() {
        return primaryKey.getMember();
    }

    @Transient
    public AttributeDefinitionEntity getDefinition() {
        return primaryKey.getDefinition();
    }
}
