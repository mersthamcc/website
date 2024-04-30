package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.beans.Transient;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

/** DTO for {@link cricket.merstham.graphql.entity.MessageEntity} */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class Message implements Serializable {
    @Serial private static final long serialVersionUID = 1111294032355797838L;
    @JsonProperty private String key;
    @JsonProperty private String messageClass;
    @JsonProperty private String messageText;
    @JsonProperty private boolean enabled;
    @JsonProperty private Instant startDate;
    @JsonProperty private Instant endDate;

    @Transient
    public boolean isCurrent() {
        var now = Instant.now();
        return enabled
                && (isNull(startDate) || startDate.isBefore(now))
                && (isNull(endDate) || now.isBefore(endDate));
    }

    @Transient
    public String getCleanMessage() {
        return Jsoup.clean(messageText, Safelist.simpleText().addTags("a"));
    }
}
