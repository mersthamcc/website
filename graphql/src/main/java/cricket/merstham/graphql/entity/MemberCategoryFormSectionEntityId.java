package cricket.merstham.graphql.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCategoryFormSectionEntityId implements Serializable {
    @Serial private static final long serialVersionUID = -5019293641200434037L;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_category_id", nullable = false)
    private MemberCategoryEntity memberCategory;

    @OneToOne(optional = false)
    @JoinColumn(name = "member_form_section_id", nullable = false)
    private MemberFormSectionEntity memberFormSection;
}
