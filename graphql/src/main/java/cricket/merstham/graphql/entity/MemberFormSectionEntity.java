package cricket.merstham.graphql.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

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
