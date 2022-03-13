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
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "news", indexes = {
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "news", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<NewsAttributeEntity> attributes = new ArrayList<>();
}