package util;

import java.util.HashMap;
import java.util.Map;

public class Render {
    private static Map<String, String> colorMap = new HashMap<>();
    private static Map<String, String> tagMap = new HashMap<>();

    /**
     * Separate thousands
     *
     * @param number the number to apply currency format to
     * @return formatted number
     */
    public static String currency(long number) {
        return String.format("%,d", number);
    }

    public static String currency(double number) {
        return String.format("%,f", number);
    }

    /**
     * Highlight message
     *
     * @param string the string to highlight
     * @param colorFlag name of a color or a HEX code
     * @param styles styles ('b' = bold, 'u' = underlined, 'i' = italic)
     * @return highlighted string
     */
    public static String highlight(String string, String colorFlag, String[] styles) {
        String color;

        if (colorMap.isEmpty()) {
            colorMap.put("Red",         "FF554D");
            colorMap.put("Orange",      "FFA500");
            colorMap.put("Yellow",      "FFFF33");
            colorMap.put("Green",       "7CEE7C");
            colorMap.put("WashedGreen", "CCFFCC");
            colorMap.put("Blue",        "87CEEB");
            colorMap.put("Pink",        "FF6699");
            colorMap.put("Prune",       "9966FF");
            colorMap.put("WashedPrune", "CCCCFF");
            colorMap.put("Gray",        "999999");
            colorMap.put("Skin",        "FFEFD5");
        }

        if (tagMap.isEmpty()) {
            tagMap.put("b", "b");
            tagMap.put("i", "i");
            tagMap.put("u", "u");
        }

        if (colorMap.containsKey(colorFlag)) {
            color = colorMap.get(colorFlag);
        } else {
            color = colorFlag;
        }

        for (String style : styles) {
            String currentStyle = style.trim().toLowerCase();
            if (tagMap.containsKey(currentStyle)) {
                String tag = tagMap.get(currentStyle);
                tagMap.remove(currentStyle);

                string = String.format("<%s>%s</%s>", tag, string, tag);
            }
        }

        if (null != color) {
            return String.format("<font color=\"#%s\">%s</font>", color, string);
        } else {
            return string;
        }
    }

    public static String highlight(String string, String colorFlag) {
        String[] flags = {};
        return highlight(string, colorFlag, flags);
    }

    public static String highlight(String string, String[] styles) {
        return highlight(string, null, styles);
    }
}
