package cricket.merstham.graphql.dto.signage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class PushToScreensRequest implements Serializable {
    @Serial private static final long serialVersionUID = -4556001589390542202L;

    @JsonProperty("use_download_timeslots")
    private boolean useDownloadTimeslots;

    @JsonProperty("filter_workspaces")
    private List<Integer> filterWorkspaces;
}
