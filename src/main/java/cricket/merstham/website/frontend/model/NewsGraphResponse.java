package cricket.merstham.website.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cricket.merstham.website.frontend.model.admintables.News;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsGraphResponse implements Serializable {
    private static final long serialVersionUID = 20211031184100L;

    @JsonProperty
    private List<News> news = List.of();

    @JsonProperty
    private int recordsTotal;

    @JsonProperty
    private int recordsFiltered;
}
