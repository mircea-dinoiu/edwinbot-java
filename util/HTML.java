package util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class HTML {
    /**
     * Remove HTML tags from string
     *
     * @param string the string to remove the HTML tags from
     * @return string with no HTML tags
     */
    public static String stripHTML(String string) {
        String[] stringParts = string.split("<");

        if (1 == stringParts.length) {
            return stringParts[0];
        } else {
            List<String> returnedParts = new ArrayList<String>();

            for (String data : stringParts) {
                String[] dataParts = data.split(">", 2);

                if (1 == dataParts.length) {
                    returnedParts.add(dataParts[0]);
                } else {
                    returnedParts.add(dataParts[1]);
                }
            }

            return util.Strings.implode(returnedParts);
        }
    }

    /**
     * Convert a string to HTML entities
     * Replace every character with its ASCII code
     *
     * @param string the string to convert
     * @return the converted string
     */
    public static String HTMLEntities(String string) {
        char[] characters = string.toCharArray();
        List<String> entities = new ArrayList<String>();

        for (char character : characters) {
            entities.add(String.format("&#%s;", (int) character));
        }

        return util.Strings.implode(entities);
    }

    /**
     * Close HTML tags
     *
     * @param html the HTML string
     * @return the HTML content with all the tags closed
     */
    public static String closeTags(String html) {
        Document doc = Jsoup.parse(html);

        return doc.body().children().toString();
    }
}
