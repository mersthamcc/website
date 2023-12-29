package cricket.merstham.graphql.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import cricket.merstham.shared.extensions.StringExtensions;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import org.apache.logging.log4j.util.Strings;

import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Entity
@Table(
        name = "contact_category",
        indexes = {
            @Index(name = "IDX_CONTACT_CATEGORY_TITLE", unique = true, columnList = "title"),
            @Index(name = "IDX_CONTACT_CATEGORY_SLUG", unique = true, columnList = "slug")
        })
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ExtensionMethod({StringExtensions.class})
public class ContactCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "slug", nullable = false, length = Integer.MAX_VALUE)
    private String slug;

    @OneToMany(mappedBy = "category")
    private List<ContactEntity> contacts = new LinkedList<>();

    @NotNull
    @Column(name = "sort_order", nullable = false)
    @JsonProperty
    private int sortOrder;

    @PrePersist
    void preInsert() {
        if (isNull(slug) || Strings.isBlank(slug)) {
            slug = title.toSlug();
        }
    }
}
