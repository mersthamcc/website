package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(
        name = "static_data",
        indexes = {@Index(name = "idx_static_data_path", columnList = "path", unique = true)})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaticDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 256)
    @NotNull
    @Column(name = "path", nullable = false, length = 256)
    private String path;

    @Size(max = 128)
    @Column(name = "content_type", length = 128)
    private String contentType;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "content", length = Integer.MAX_VALUE)
    private String content;
}
