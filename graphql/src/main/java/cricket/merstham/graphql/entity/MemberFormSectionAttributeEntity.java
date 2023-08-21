package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_form_section_attribute")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberFormSectionAttributeEntity {
    @EmbeddedId private MemberFormSectionAttributeEntityId primaryKey;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "mandatory", nullable = false)
    private Boolean mandatory = false;

    @Transient
    public MemberFormSectionEntity getSection() {
        return primaryKey.getMemberFormSection();
    }

    @Transient
    public AttributeDefinitionEntity getDefinition() {
        return primaryKey.getAttributeDefinition();
    }
}
