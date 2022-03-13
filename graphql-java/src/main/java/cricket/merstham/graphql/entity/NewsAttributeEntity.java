package cricket.merstham.graphql.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "news_attribute", indexes = {
        @Index(name = "idx_unique_news_attribute_news_id_name", columnList = "news_id, name", unique = true),
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