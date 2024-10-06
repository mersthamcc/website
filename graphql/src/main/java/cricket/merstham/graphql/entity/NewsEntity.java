package cricket.merstham.graphql.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(
        name = "news",
        indexes = {
            @Index(name = "idx_unique_news_uuid", columnList = "uuid", unique = true),
            @Index(name = "idx_unique_news_path", columnList = "path", unique = true)
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsEntity implements Serializable {

    private static final long serialVersionUID = -3185758010176877847L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "publish_date", nullable = false)
    private Instant publishDate;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "author")
    private String author;

    @Column(name = "path")
    private String path;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "draft")
    private Boolean draft;

    @Column(name = "social_summary")
    private String socialSummary;

    @Column(name = "feature_image_url")
    private String featureImageUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "news_attribute", joinColumns = @JoinColumn(name = "news_id"))
    private Map<String, String> attributes = new HashMap<>();
}
