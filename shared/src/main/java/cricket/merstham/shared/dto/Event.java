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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ofPattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@ExtensionMethod(StringExtensions.class)
public class Event implements Serializable {

    @JsonProperty("id")
    private int id;

    @JsonProperty("event_date")
    private Instant eventDate;

    @JsonProperty("title")
    private String title;

    @JsonProperty("path")
    private String path;

    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("location")
    private String location;

    @JsonProperty("body")
    private String body;

    @JsonProperty("callToActionLink")
    private String callToActionLink;

    @JsonProperty("callToActionDescription")
    private String callToActionDescription;

    @JsonProperty("banner")
    private String banner;

    @JsonProperty("attributes")
    private List<KeyValuePair> attributes;

    public LocalDateTime getDisplayDate() {
        return LocalDateTime.ofInstant(eventDate, ZoneId.systemDefault());
    }

    public String getAbstract() {
        return body.toAbstract();
    }

    public String getFormattedDate() {
        return eventDate.atZone(UTC).format(ofPattern("dd/MM/yyyy"));
    }

    public String getSlug() {
        return title.toSlug();
    }
}
