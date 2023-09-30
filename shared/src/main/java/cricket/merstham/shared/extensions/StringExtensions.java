package cricket.merstham.shared.extensions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

import static java.util.Objects.isNull;

public class StringExtensions {
    public static String toSlug(String in) {
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

    public static boolean isNumeric(String in) {
        if (in == null) {
            return false;
        }
        try {
            Integer.parseInt(in);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static String toAbstract(String in) {
        Document doc = Jsoup.parse(in);
        Element readMoreAnchor = doc.selectFirst("a#readmore");

        if (isNull(readMoreAnchor)) {
            return Jsoup.clean(in, Safelist.basic());
        }

        return Jsoup.clean(
                in.substring(0, in.indexOf(readMoreAnchor.outerHtml())), Safelist.basic());
    }
}
