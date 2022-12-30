package cricket.merstham.shared.utils;

import lombok.Data;

import java.util.Base64;
import java.util.Objects;
import java.util.regex.Pattern;

@Data
public class DataURI {

    private static final Pattern PATTERN =
            Pattern.compile("^data:((?:\\w+\\/(?:(?!;).)+)?)((?:;[\\w\\W]*?[^;])*),(.+)$");
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
            if (Objects.equals(result.group(2), ";base64")) {
                var bytes = Base64.getDecoder().decode(result.group(3));
                return new DataURI(result.group(1), bytes);
            }
            throw new IllegalArgumentException("Encoding scheme not supported");
        }
        throw new IllegalArgumentException("Argument not a RFC2397 string");
    }
}
