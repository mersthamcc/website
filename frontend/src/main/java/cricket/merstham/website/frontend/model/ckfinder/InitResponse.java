package cricket.merstham.website.frontend.model.ckfinder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class InitResponse {
    private List<ResourceType> resourceTypes;
    private ImageConfig images;
    private String s;
    private String c;
    private boolean uploadCheckImages;
    private boolean enabled;
    private List<String> thumbs;
}
