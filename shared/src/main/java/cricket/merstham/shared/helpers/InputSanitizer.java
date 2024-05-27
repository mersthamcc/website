package cricket.merstham.shared.helpers;

import org.owasp.encoder.Encode;

public final class InputSanitizer {

    public static String encodeForLog(String input) {
        return Encode.forJava(input).replaceAll("[\n\r]", "_");
    }

    private InputSanitizer() {}
}
