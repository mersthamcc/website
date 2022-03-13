package cricket.merstham.graphql.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "member_form_section_attribute")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberFormSectionAttributeEntity {
    @EmbeddedId
    private MemberFormSectionAttributeEntityId primaryKey;

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