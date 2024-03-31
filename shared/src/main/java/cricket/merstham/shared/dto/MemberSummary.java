package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.beans.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize
@Accessors(chain = true)
public class MemberSummary implements Serializable {
    @JsonProperty private Integer id;
    @JsonProperty private String ownerUserId;
    @JsonProperty private String familyName;
    @JsonProperty private String givenName;
    @JsonProperty private Instant firstRegistrationDate;
    @JsonProperty private LocalDate dob;
    @JsonProperty private String ageGroup;
    @JsonProperty private String gender;
    @JsonProperty private Integer mostRecentSubscription;
    @JsonProperty private LocalDate lastSubsDate;
    @JsonProperty private BigDecimal lastSubsPrice;
    @JsonProperty private String lastSubsCategory;
    @JsonProperty private BigDecimal received;
    @JsonProperty private String paymentTypes;
    @JsonProperty private String description;
    @JsonProperty private String uuid;
    @JsonProperty private String applePassSerial;
    @JsonProperty private List<String> declarations;
    @JsonProperty private List<String> identifiers;

    @JsonProperty
    @Transient
    public String getLastSubsYear() {
        return Integer.toString(lastSubsDate.getYear());
    }
}
