package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cricket.merstham.shared.extensions.StringExtensions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@ExtensionMethod(StringExtensions.class)
public class StaticPage implements Serializable {
    @Serial private static final long serialVersionUID = -947526036574542772L;

    @JsonProperty private String slug;
    @JsonProperty private String title;
    @JsonProperty private String content;
    @JsonProperty private int sortOrder;
    @JsonProperty private String menu;

    public String getAbstract(int paragraphs) {
        return content.toAbstract(paragraphs);
    }
}
