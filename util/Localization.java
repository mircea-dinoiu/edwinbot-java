package util;

public class Localization {
    final private static String DEFAULT_LANGUAGE = "en";

    /**
     * Format number by locale
     * Supports negative numbers
     *
     * @param number number to format
     * @param manyText text to use when the number is not 1
     * @param oneText text to use when number equals 1
     * @param language language to use for formatting
     * @param niceNumber string containing a formatted number (like currency or highlighted) to use for the output
     * @return String
     */
    public static String formatNumber(Number number, String manyText, String oneText, String language, String niceNumber) {
        String output;

        if (!util.Numbers.hasDecimals(number)) {
            number = number.longValue();
        }

        if (language.equals("ro")) {
            if (1 == Math.abs(number.doubleValue())) {
                output = String.format("%s %s", niceNumber, oneText);
            } else {
                if (util.Numbers.hasDecimals(number)) {
                    number = util.Numbers.getFractionalPartAsWhole(number);
                }

                int lastTwoDigits = Math.abs(number.intValue() % 100);

                if (number.equals(0)
                    || (lastTwoDigits > 0 && lastTwoDigits < 20)) {
                    output = String.format("%s %s", niceNumber, manyText);
                } else {
                    output = String.format("%s de %s", niceNumber, manyText);
                }
            }
        } else if (language.equals("en")) {
            if (1 == Math.abs(number.doubleValue())) {
                output = String.format("%s %s", niceNumber, oneText);
            } else {
                output = String.format("%s %s", niceNumber, manyText);
            }
        } else {
            output = niceNumber;
        }

        return output;
    }

    public static String formatNumber(Number number, String manyText, String oneText, String language) {
        if (!util.Numbers.hasDecimals(number)) {
            number = number.longValue();
        }
        return formatNumber(number, manyText, oneText, language, String.valueOf(number));
    }

    public static String formatNumber(Number number, String manyText, String oneText) {
        return formatNumber(number, manyText, oneText, DEFAULT_LANGUAGE);
    }
}
