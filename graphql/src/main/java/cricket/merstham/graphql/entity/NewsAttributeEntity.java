package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(
        name = "news_attribute",
        indexes = {
            @Index(
                    name = "idx_unique_news_attribute_news_id_name",
                    columnList = "news_id, name",
                    unique = true),
            @Index(name = "idx_news_attribute_news_id", columnList = "news_id")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsAttributeEntity implements Serializable {

    private static final long serialVersionUID = 8499895244401317062L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "news_id", nullable = false)
    private NewsEntity news;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "value", length = 1024)
    private String value;
}
