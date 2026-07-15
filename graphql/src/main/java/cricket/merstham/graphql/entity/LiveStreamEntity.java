package cricket.merstham.graphql.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder(toBuilder = true)
@Table(
        name = "live_stream",
        indexes = {
            @Index(name = "idx_live_stream_youtube_id", columnList = "youtube_id", unique = true),
            @Index(name = "idx_live_stream_fixture_id", columnList = "fixture_id")
        })
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class LiveStreamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 128)
    @NotNull
    @Column(name = "title", nullable = false, length = 128)
    private String title;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fixture_id")
    private FixtureEntity fixture;

    @Size(max = 128)
    @NotNull
    @Column(name = "frogbox_id", nullable = false, length = 128)
    private String frogboxId;

    @Size(max = 128)
    @NotNull
    @Column(name = "youtube_id", nullable = false, length = 128)
    private String youtubeId;

    @Size(max = 128)
    @Column(name = "signage_id", length = 128)
    private String signageId;

    @Size(max = 128)
    @Column(name = "signage_schedule_id", length = 128)
    private String signageScheduleId;

    @Size(max = 512)
    @Column(name = "thumbnail_url", length = 512)
    private String thumbnailUrl;

    @Column(name = "widget")
    private String widget;
}
