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
        return toAbstract(in, 2);
    }

    public static String toAbstract(String in, int paragraphCount) {
        Document doc = Jsoup.parse(in);
        Elements paragraphs = doc.select("p");
        String result = in;

        if (paragraphs.size() > paragraphCount) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < paragraphCount; i++) {
                builder.append(paragraphs.get(i).outerHtml());
            }
            result = builder.toString();
        }

        return Jsoup.clean(result, Safelist.basic());
    }
}
