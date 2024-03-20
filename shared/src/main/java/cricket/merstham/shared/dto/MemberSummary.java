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

/** DTO for {@link cricket.merstham.graphql.entity.MemberSummaryEntity} */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize
@Accessors(chain = true)
public class MemberSummary implements Serializable {
    @JsonProperty Integer id;
    @JsonProperty String familyName;
    @JsonProperty String givenName;
    @JsonProperty Instant firstRegistrationDate;
    @JsonProperty LocalDate dob;
    @JsonProperty String ageGroup;
    @JsonProperty String gender;
    @JsonProperty Integer mostRecentSubscription;
    @JsonProperty LocalDate lastSubsDate;
    @JsonProperty BigDecimal lastSubsPrice;
    @JsonProperty String lastSubsCategory;
    @JsonProperty BigDecimal received;
    @JsonProperty String paymentTypes;
    @JsonProperty String description;
    @JsonProperty List<String> declarations;

    @JsonProperty
    @Transient
    public String getLastSubsYear() {
        return Integer.toString(lastSubsDate.getYear());
    }
}
