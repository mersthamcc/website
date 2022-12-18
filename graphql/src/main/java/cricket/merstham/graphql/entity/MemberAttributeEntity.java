package cricket.merstham.graphql.entity;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.io.Serializable;
import java.time.Instant;

import static cricket.merstham.graphql.configuration.HibernateConfiguration.ENCRYPTED_JSON_TYPE;

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

    @EmbeddedId private MemberAttributeEntityId primaryKey = new MemberAttributeEntityId();

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "updated_date", nullable = false)
    private Instant updatedDate;

    @Column(name = "value")
    @Type(type = ENCRYPTED_JSON_TYPE)
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
