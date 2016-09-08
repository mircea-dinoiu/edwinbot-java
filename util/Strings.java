package util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Strings {
    final private static String DEFAULT_IMPLODE_GLUE = "";
    final private static String DEFAULT_EXPLODE_DELIMITER = " ";
    final private static int DEFAULT_EXPLODE_LIMIT = 0;

    /**
     * Converts a list of bytes to a string.
     *
     * @param list the list of bytes
     * @return the string created from the list of bytes
     */
    public static String byteListToString(List<Byte> list) {
        byte[] array = new byte[list.size()];
        int i = 0;

        for (Byte current : list) {
            array[i] = current;
            i++;
        }

        return new String(array, Charset.forName("UTF-8"));
    }

    public static String[] numbersToStrings(Number[] array) {
        List<String> strings = new ArrayList<>();

        for (Number number : array) {
            strings.add(String.valueOf(number));
        }

        return strings.toArray(new String[strings.size()]);
    }

    public static String[] numbersToStrings(List<Number> list) {
        Number[] array = list.toArray(new Number[list.size()]);
        return numbersToStrings(array);
    }

    /**
     * Get the substring between two substrings in a string
     *
     * @param string source string
     * @param before left string delimiter
     * @param after right string delimiter
     * @return the string between the `before` and `after` strings
     */
    public static String everythingBetween(String string, String before, String after) {
        int start, end;

        start = string.indexOf(before) + before.length();
        end = string.indexOf(after, start);

        return string.substring(start, end);
    }

    /**
     * Implode util function.
     *
     * @param glue separator to use when joining an array of strings
     * @param array the array to join
     * @return the joined array
     */
    public static String implode(String glue, String[] array) {
        StringBuilder buff = new StringBuilder();
        int positionInArray = 0;

        for (String each : array) {
            buff.append(each);

            if (positionInArray != array.length - 1 && 1 != array.length) {
                buff.append(glue);
            }

            positionInArray++;
        }
        return buff.toString();
    }

    public static String implode(String glue, List<String> list) {
        String[] array = list.toArray(new String[list.size()]);
        return implode(glue, array);
    }

    public static String implode(String[] array) {
        return implode(DEFAULT_IMPLODE_GLUE, array);
    }

    public static String implode(List<String> list) {
        String[] array = list.toArray(new String[list.size()]);
        return implode(array);
    }

    /**
     * Split a string by a separator and trims all the elements returning an array of non-empty strings
     *
     * @param string the string to explode
     * @param delimiter the delimiter
     * @param limit limit the explosions
     * @return array of non-empty strings
     */
    public static String[] explode(String string, String delimiter, int limit) {
        String[] stringParts = string.split(Pattern.quote(delimiter), limit);
        List<String> nonEmptyStrings = new ArrayList<>();

        for (String each : stringParts) {
            each = each.trim();
            if (each.length() > 0) {
                nonEmptyStrings.add(each);
            }
        }

        return nonEmptyStrings.toArray(new String[nonEmptyStrings.size()]);
    }

    public static String[] explode(String string, String delimiter) {
        return explode(string, delimiter, DEFAULT_EXPLODE_LIMIT);
    }

    public static String[] explode(String string, int limit) {
        return explode(string, DEFAULT_EXPLODE_DELIMITER, limit);
    }

    public static String[] explode(String string) {
        return explode(string, DEFAULT_EXPLODE_DELIMITER, DEFAULT_EXPLODE_LIMIT);
    }
}
