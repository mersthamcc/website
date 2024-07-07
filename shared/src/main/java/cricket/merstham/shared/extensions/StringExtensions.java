package cricket.merstham.shared.extensions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

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
        Elements paragraphs = doc.select("p");
        String result = in;

        if (paragraphs.size() > 2) {
            result = paragraphs.get(0).outerHtml() + paragraphs.get(1).outerHtml();
        }

        return Jsoup.clean(result, Safelist.basic());
    }
}
