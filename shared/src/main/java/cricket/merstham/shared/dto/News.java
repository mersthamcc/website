package cricket.merstham.shared.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cricket.merstham.shared.extensions.StringExtensions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
@ExtensionMethod({StringExtensions.class})
public class News implements Serializable {
    private static final long serialVersionUID = 20211024150500L;

    @JsonProperty private int id;

    @JsonProperty private String title;

    @JsonProperty private String author;

    @JsonProperty private String body;

    @JsonProperty private String uuid;

    @JsonProperty private String path;

    @JsonProperty private Instant createdDate;

    @JsonProperty private Instant publishDate;

    @JsonProperty private boolean draft;

    @JsonProperty private boolean publishToFacebook;

    @JsonProperty private boolean publishToTwitter;

    @JsonProperty private String socialSummary;

    @JsonProperty private List<KeyValuePair> attributes;

    @JsonProperty("formattedPublishDate")
    public String getFormattedPublishDate() {
        return publishDate.atZone(UTC).format(ofPattern("dd/MM/yyyy"));
    }

    public String getDisplayPublishDate() {
        return publishDate.atZone(UTC).format(ofPattern("d MMMM, yyyy"));
    }

    public String getAbstract() {
        Document doc = Jsoup.parse(body);
        Element readMoreAnchor = doc.selectFirst("a#readmore");

        if (isNull(readMoreAnchor)) {
            return Jsoup.clean(body, Safelist.basic());
        }

        return Jsoup.clean(
                body.substring(0, body.indexOf(readMoreAnchor.outerHtml())), Safelist.basic());
    }

    public String getAuthorInitials() {
        return String.join(
                "",
                Arrays.stream(author.split(" "))
                        .map(n -> n.toUpperCase(Locale.ROOT).substring(0, 1))
                        .collect(Collectors.toList()));
    }

    public List<Image> getImages() {
        return Jsoup.parse(body).select("img").stream()
                .map(i -> Image.builder().path(i.attr("src")).caption(i.attr("alt")).build())
                .collect(Collectors.toList());
    }

    public boolean hasImages() {
        return getImages().size() > 0;
    }

    public String getSlug() {
        return title.toSlug();
    }

    public String getAttribute(String key) {
        return isNull(attributes)
                ? null
                : getAttributes().stream()
                        .filter(a -> a.getKey().equals(key))
                        .findFirst()
                        .map(a -> a.getValue())
                        .orElse(null);
    }

    public News setAttribute(String key, String value) {
        var existing = getAttributes().stream().filter(a -> a.getKey().equals(key)).findFirst();
        existing.ifPresentOrElse(
                keyValuePair -> keyValuePair.setValue(value),
                () -> {
                    getAttributes().add(KeyValuePair.builder().key(key).value(value).build());
                });
        return this;
    }

    public boolean hasAttribute(String key) {
        return isNull(attributes) && getAttributes().stream().anyMatch(a -> a.getKey().equals(key));
    }

    public Map<String, String> getAttributeMap() {
        return isNull(attributes)
                ? Map.of()
                : getAttributes().stream()
                        .collect(Collectors.toMap(KeyValuePair::getKey, KeyValuePair::getValue));
    }
}
