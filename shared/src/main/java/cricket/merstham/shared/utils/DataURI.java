package cricket.merstham.shared.utils;

import lombok.Data;

import java.util.Base64;
import java.util.Objects;
import java.util.regex.Pattern;

@Data
public class DataURI {

    private static final Pattern PATTERN =
            Pattern.compile("data:(?<type>\\w+\\/\\w+);(?<encoding>\\w+),(?<data>.+)$");
    private final byte[] content;
    private final String mediaType;

    private DataURI(String mediaType, byte[] content) {
        this.mediaType = mediaType;
        this.content = content;
    }

    public static DataURI parse(String src) {
        if (!src.startsWith("data:")) {
            throw new IllegalArgumentException("Argument not a RFC2397 string");
        }
        var result = PATTERN.matcher(src);
        if (result.matches()) {
            if (Objects.equals(result.group("encoding"), "base64")) {
                var bytes = Base64.getDecoder().decode(result.group("data"));
                return new DataURI(result.group("type"), bytes);
            }
            throw new IllegalArgumentException("Encoding scheme not supported");
        }
        throw new IllegalArgumentException("Argument not a RFC2397 string");
    }
}
