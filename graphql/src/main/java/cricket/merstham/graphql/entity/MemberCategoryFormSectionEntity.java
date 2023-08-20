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
