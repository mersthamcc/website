package cricket.merstham.website.frontend.extensions;

import org.jetbrains.annotations.NotNull;

public class StringExtensions {
    public static String toSlug(@NotNull String in) {
        if (in == null || in.trim().length() == 0) {
            throw new IllegalArgumentException();
        }
        return String.join(
                        "-",
                        in.toLowerCase()
                                .replace("\n", " ")
                                .replaceAll("[^a-z\\d\\s]", " ")
                                .split(" "))
                .replaceAll("-+", "-");
    }
}
