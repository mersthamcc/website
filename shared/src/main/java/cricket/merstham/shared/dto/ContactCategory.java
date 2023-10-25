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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@ExtensionMethod(StringExtensions.class)
public class ContactCategory implements Serializable {
    @JsonProperty private Integer id;
    @JsonProperty private String title;
    @JsonProperty private String slug;
    @JsonProperty private List<Contact> contacts;
}
