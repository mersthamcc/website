package cricket.merstham.website.frontend.model.ckfinder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.net.URI;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceType {
    private String allowedExtensions;
    private String deniedExtensions;
    private boolean hasChildren;
    private String name;
    private int acl;
    private String label;
    private boolean lazyLoad;
    private String hash;
    private URI url;
}
