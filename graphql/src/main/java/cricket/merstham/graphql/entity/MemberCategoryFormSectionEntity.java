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
@Table(name = "member_category_form_section")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCategoryFormSectionEntity {
    @EmbeddedId private MemberCategoryFormSectionEntityId primaryKey;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "show_on_registration", nullable = false)
    private Boolean showOnRegistration = false;

    @Transient
    public MemberCategoryEntity getCategory() {
        return primaryKey.getMemberCategory();
    }

    @Transient
    public MemberFormSectionEntity getSection() {
        return primaryKey.getMemberFormSection();
    }
}
