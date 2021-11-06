package cricket.merstham.website.frontend.model.admintables;

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

import java.io.Serializable;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static cricket.merstham.website.frontend.helpers.RoutesHelper.ADMIN_NEWS_EDIT_ROUTE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.NEWS_ROUTE_TEMPLATE;
import static cricket.merstham.website.frontend.helpers.RoutesHelper.buildRoute;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper=false)
@ExtensionMethod({StringExtensions.class})
public class News extends SspBaseResponseData implements Serializable {
    private static final long serialVersionUID = 20211024150500L;

    @JsonProperty
    private int id;

    @JsonProperty
    private String title;

    @JsonProperty
    private String author;

    @JsonProperty
    private String body;

    @JsonProperty
    private LocalDateTime createdDate;

    @JsonProperty
    private LocalDateTime publishDate;

    public URI getEditLink() {
        return buildRoute(ADMIN_NEWS_EDIT_ROUTE, Map.of("id", id));
    }

    public URI getLink() {
        return buildRoute(NEWS_ROUTE_TEMPLATE, Map.of(
                "year", publishDate.getYear(),
                "month", publishDate.format(DateTimeFormatter.ofPattern("MM")),
                "day", publishDate.format(DateTimeFormatter.ofPattern("dd")),
                "slug", title.toSlug()
        ));
    }
}
