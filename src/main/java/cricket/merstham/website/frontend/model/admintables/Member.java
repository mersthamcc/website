package cricket.merstham.website.frontend.model.admintables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cricket.merstham.website.frontend.model.datatables.SspBaseResponseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.net.URI;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper=false)
public class Member extends SspBaseResponseData implements Serializable {
    private static final long serialVersionUID = 20210927233800L;

    @JsonProperty private String id;

    @JsonProperty private String familyName;

    @JsonProperty private String givenName;

    @JsonProperty private String category;

    @JsonProperty private String lastSubscription;

    @JsonProperty private URI editLink;
}
