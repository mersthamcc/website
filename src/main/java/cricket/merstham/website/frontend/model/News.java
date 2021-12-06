package cricket.merstham.website.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cricket.merstham.website.frontend.extensions.StringExtensions;
import cricket.merstham.website.frontend.model.datatables.SspBaseResponseData;
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
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_NEWS_DELETE_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_NEWS_EDIT_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.NEWS_ROUTE_TEMPLATE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.buildRoute;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
@ExtensionMethod({StringExtensions.class})
public class News extends SspBaseResponseData implements Serializable {
    private static final long serialVersionUID = 20211024150500L;

    @JsonProperty private int id;

    @JsonProperty private String title;

    @JsonProperty private String author;

    @JsonProperty private String body;

    @JsonProperty private String uuid;

    @JsonProperty private LocalDateTime createdDate;

    @JsonProperty private LocalDateTime publishDate;

    @JsonProperty private boolean draft;

    public URI getEditLink() {
        return buildRoute(ADMIN_NEWS_EDIT_ROUTE, Map.of("id", id));
    }

    public URI getDeleteLink() {
        return buildRoute(ADMIN_NEWS_DELETE_ROUTE, Map.of("id", id));
    }

    public URI getLink() {
        return buildRoute(
                NEWS_ROUTE_TEMPLATE,
                Map.of(
                        "year", publishDate.getYear(),
                        "month", publishDate.format(ofPattern("MM")),
                        "day", publishDate.format(ofPattern("dd")),
                        "slug", title.toSlug()));
    }

    @JsonProperty("formattedPublishDate")
    public String getFormattedPublishDate() {
        return publishDate.format(ofPattern("dd/MM/YYYY"));
    }

    public String getDisplayPublishDate() {
        return publishDate.format(ofPattern("d MMMM, yyyy"));
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
}
