package cricket.merstham.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class News implements Serializable {
    private static final long serialVersionUID = -1297191973585788108L;

    private Integer id;
    private Instant createdDate;
    private Instant publishDate;
    private String title;
    private String body;
    private String author;
    private String path;
    private String uuid;
    private Boolean draft;
    private String socialSummary;
    private List<NewsAttribute> attributes;
}
