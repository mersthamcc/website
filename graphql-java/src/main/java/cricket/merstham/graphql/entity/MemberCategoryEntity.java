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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member_category", indexes = {
        @Index(name = "idx_user_key", columnList = "key", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "key", nullable = false, length = 64)
    private String key;

    @Column(name = "registration_code", length = 64)
    private String registrationCode;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "primaryKey.memberCategory", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("sortOrder")
    private List<MemberCategoryFormSectionEntity> form = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "memberCategory", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("description")
    private List<PricelistItemEntity> pricelistItem = new ArrayList<>();
}