package cricket.merstham.shared.extensions;

import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;

public class OrdinalExtensions {
    public static String getOrdinal(Integer value) {
        if (isNull(value)) return "";
        var number = value.intValue();
        int mod = number;
        if (number > 13) {
            mod = number % 10;
        }
        switch (mod) {
            case 1:
                return format("{0}st", number);
            case 2:
                return format("{0}nd", number);
            case 3:
                return format("{0}rd", number);
            default:
                return format("{0}th", number);
        }
    }
}
