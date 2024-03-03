package cricket.merstham.graphql.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "event")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "event_date", nullable = false)
    private Instant eventDate;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "path")
    private String path;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "location")
    private String location;

    @Column(name = "body", nullable = false, length = Integer.MAX_VALUE)
    private String body;

    @Column(name = "cta_link", length = Integer.MAX_VALUE)
    private String callToActionLink;

    @Column(name = "cta_description", length = Integer.MAX_VALUE)
    private String callToActionDescription;

    @Column(name = "banner", length = Integer.MAX_VALUE)
    private String banner;

    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "event_attribute", joinColumns = @JoinColumn(name = "event_id"))
    private Map<String, String> attributes = new HashMap<>();
}
