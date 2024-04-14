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

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "member_category",
        indexes = {@Index(name = "idx_user_key", columnList = "key", unique = true)})
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

    @Column(name = "sort_order")
    private int sortOrder;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "primaryKey.memberCategory",
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    @OrderBy("sortOrder")
    private List<MemberCategoryFormSectionEntity> form = new ArrayList<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "memberCategory",
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    @OrderBy("description")
    private List<PricelistItemEntity> pricelistItem = new ArrayList<>();
}
