package cricket.merstham.graphql.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "static_page")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaticPageEntity {
    @Id
    @Column(name = "slug", nullable = false)
    @JsonProperty
    private String slug;

    @Size(max = 255)
    @NotNull
    @Column(name = "title", nullable = false)
    @JsonProperty
    private String title;

    @NotNull
    @Column(name = "content", nullable = false)
    @JsonProperty
    private String content;

    @NotNull
    @Column(name = "sort_order", nullable = false)
    @JsonProperty
    private int sortOrder;

    @Column(name = "menu", length = Integer.MAX_VALUE)
    private String menu;
}
