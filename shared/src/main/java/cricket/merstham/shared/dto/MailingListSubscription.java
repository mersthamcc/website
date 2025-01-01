package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class MailingListSubscription implements Serializable {
    @Serial private static final long serialVersionUID = 3885575540890631857L;
    private String emailAddress;
    private boolean subscribed;
    private boolean pending;
    private boolean manualSubscriptionRequired;
}
