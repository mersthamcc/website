package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/** DTO for {@link cricket.merstham.graphql.entity.StaticDataEntity} */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
@Builder
public class StaticData implements Serializable {
    @JsonProperty private Integer id;
    @JsonProperty private String path;
    @JsonProperty private String contentType;
    @JsonProperty private Integer statusCode;
    @JsonProperty private String content;
}
