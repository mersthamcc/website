package cricket.merstham.website.frontend.model.admintables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cricket.merstham.website.frontend.model.datatables.SspBaseResponseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class Member extends SspBaseResponseData {

    @JsonProperty
    private String id;

    @JsonProperty
    private String familyName;

    @JsonProperty
    private String givenName;

    @JsonProperty
    private String category;

    @JsonProperty
    private String lastSubscription;

    @JsonProperty
    private URI editLink;
}
