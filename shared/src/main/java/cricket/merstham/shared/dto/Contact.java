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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@ExtensionMethod(StringExtensions.class)
public class Contact implements Serializable {
    @JsonProperty private int id;
    @JsonProperty private ContactCategory category;
    @JsonProperty private String position;
    @JsonProperty private String slug;
    @JsonProperty private String name;
    @JsonProperty private List<KeyValuePair> methods;

    public Map<String, String> getAttributeMap() {
        return isNull(methods)
                ? Map.of()
                : getMethods().stream()
                .collect(
                        Collectors.toMap(
                                KeyValuePair::getKey,
                                KeyValuePair::getValue));
    }
}
