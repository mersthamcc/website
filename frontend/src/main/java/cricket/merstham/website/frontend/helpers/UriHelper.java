package cricket.merstham.website.frontend.helpers;

import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;

import static java.util.Objects.isNull;

public class UriHelper {

    public static String resolveUrl(String baseUrl, String url) {
        if (isNull(url)) return baseUrl;
        URI uri = URI.create(url);
        if (uri.isAbsolute()) {
            return url;
        }
        return UriBuilder.fromUri(URI.create(baseUrl)).path(url).build().toString();
    }
}
