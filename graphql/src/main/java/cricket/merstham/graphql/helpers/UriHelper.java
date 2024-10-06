package cricket.merstham.graphql.helpers;

import jakarta.ws.rs.core.UriBuilder;

import static java.util.Objects.isNull;

public class UriHelper {

    public static String resolveUrl(String baseUrl, String... paths) {
        if (isNull(paths)) return baseUrl;
        var url = UriBuilder.fromUri(baseUrl);
        for (String path : paths) {
            url.path(path);
        }
        return url.build().toString();
    }
}
