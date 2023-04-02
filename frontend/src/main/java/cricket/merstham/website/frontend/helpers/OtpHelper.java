package cricket.merstham.website.frontend.helpers;

import java.util.Map;
import java.util.stream.Collectors;

public class OtpHelper {

    public static final String OTP_CODE_FIELD_PREFIX = "code";

    public static String getCodeFromMap(Map<String, String> parameters) {
        return getCodeFromMap(parameters, OTP_CODE_FIELD_PREFIX);
    }

    public static String getCodeFromMap(Map<String, String> parameters, String fieldPrefix) {
        return parameters.entrySet().stream()
                .filter(p -> p.getKey().startsWith(fieldPrefix))
                .sorted(Map.Entry.comparingByKey())
                .map(p -> p.getValue())
                .collect(Collectors.joining());
    }

    public static String getCodeFromRequestParameters(Map<String, String[]> parameters) {
        return getCodeFromRequestParameters(parameters, OTP_CODE_FIELD_PREFIX);
    }

    public static String getCodeFromRequestParameters(
            Map<String, String[]> parameters, String fieldPrefix) {
        return getCodeFromMap(
                parameters.entrySet().stream()
                        .collect(Collectors.toMap(o -> o.getKey(), o -> o.getValue()[0])),
                fieldPrefix);
    }
}
