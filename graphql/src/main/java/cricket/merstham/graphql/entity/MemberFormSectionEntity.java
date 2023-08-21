package cricket.merstham.graphql.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(
        name = "member_form_section",
        indexes = {@Index(name = "idx_member_form_section_key", columnList = "key", unique = true)})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberFormSectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "key", nullable = false, length = 64)
    private String key;

    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = "primaryKey.memberFormSection",
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    @OrderBy("sortOrder")
    private List<MemberFormSectionAttributeEntity> attribute;
}
