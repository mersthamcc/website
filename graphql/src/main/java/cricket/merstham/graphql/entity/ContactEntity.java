package cricket.merstham.graphql.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import cricket.merstham.graphql.jpa.JpaEncryptedStringConverter;
import cricket.merstham.shared.extensions.StringExtensions;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

@Getter
@Setter
@Entity
@Table(
        name = "contact",
        indexes = {@Index(name = "IDX_CONTACT_CATEGORY_ID", columnList = "category_id")})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ExtensionMethod({StringExtensions.class})
public class ContactEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JsonProperty
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonProperty
    private ContactCategoryEntity category;

    @Size(max = 255)
    @NotNull
    @Column(name = "\"position\"", nullable = false)
    @JsonProperty
    private String position;

    @NotNull
    @Column(name = "slug", nullable = false)
    @JsonProperty
    private String slug;

    @NotNull
    @Column(name = "name", nullable = false)
    @Convert(converter = JpaEncryptedStringConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @JsonProperty
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "\"method\"")
    @Column(name = "value")
    @Convert(attributeName = "value", converter = JpaEncryptedStringConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @CollectionTable(name = "contact_method", joinColumns = @JoinColumn(name = "contact_id"))
    @JsonProperty
    private Map<String, String> methods = new HashMap<>();

    @NotNull
    @Column(name = "sort_order", nullable = false)
    @JsonProperty
    private int sortOrder;

    @PrePersist
    void preInsert() {
        if (isNull(slug) || slug.isBlank()) {
            slug = position.toSlug();
        }
    }
}
