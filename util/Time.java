package util;

import manager.Language;

import java.util.*;

public class Time {
    final private static boolean DEFAULT_RETURN_DECIMAL = false;

    /**
     * Format seconds to short string
     * E.g. if seconds = 300125, the method will return 3d 11h 22m 5s
     * Doesn't support negative numbers!
     *
     * @param seconds seconds
     * @param returnDecimal true to consider milliseconds
     * @return the formatted string
     */
    public static String formatSecondsToShortString(Number seconds, boolean returnDecimal) {
        if (!util.Numbers.hasDecimals(seconds)) {
            seconds = seconds.longValue();
        } else if (!returnDecimal) {
            seconds = Math.round(seconds.floatValue());
        }

        if (0 == seconds.floatValue()) {
            return "0s";
        } else {
            List<String> ret = new ArrayList<>();
            HashMap<String, Integer> mapping = new LinkedHashMap<>();
            Number fractionalPart = util.Numbers.getFractionalPart(seconds);

            seconds = seconds.longValue();

            mapping.put("year", 3600 * 24 * 365);
            mapping.put("month", 3600 * 24 * 30);
            mapping.put("day", 3600 * 24);
            mapping.put("hour", 3600);
            mapping.put("minute", 60);
            mapping.put("second", 1);

            for (Map.Entry<String, Integer> each : mapping.entrySet()) {
                long value = seconds.longValue() / each.getValue();
                String stringValue = "";

                if (value > 0) {
                    if (!each.getKey().equals("second")) {
                        seconds = seconds.longValue() - each.getValue() * value;
                    }

                    if (each.getKey().equals("second")) {
                        if (util.Numbers.hasDecimals(fractionalPart)) {
                            stringValue = String.valueOf(value + fractionalPart.floatValue());
                        } else {
                            stringValue = String.valueOf(value);
                        }
                    } else {
                        stringValue = String.valueOf(value);
                    }
                } else if (each.getKey().equals("second")
                            && util.Numbers.hasDecimals(fractionalPart)) {
                    stringValue = String.valueOf(fractionalPart.floatValue());
                }

                if (stringValue.length() > 0) {
                    ret.add(String.format(
                        "%s%s",
                        stringValue,
                        each.getKey().charAt(0)
                    ));
                }
            }

            return util.Strings.implode(" ", ret);
        }
    }

    public static String formatSecondsToShortString(Number seconds) {
        return formatSecondsToShortString(seconds, DEFAULT_RETURN_DECIMAL);
    }

    /**
     * Format seconds to long string
     * E.g.: if seconds = 300125, the method will return 3 days, 11 hours, 22 minutes and 5 seconds
     * Doesn't support negative numbers!
     *
     * @param seconds seconds
     * @param language language to use
     * @param returnDecimal true to consider milliseconds
     * @return the formatted string
     */
    public static String formatSecondsToLongString(Number seconds, String language, boolean returnDecimal) {
        if (!util.Numbers.hasDecimals(seconds)) {
            seconds = seconds.longValue();
        } else if (!returnDecimal) {
            seconds = Math.round(seconds.floatValue());
        }

        if (0 == seconds.floatValue()) {
            return util.Localization.formatNumber(
                0,
                Language.get("SECONDS", language),
                Language.get("SECOND", language),
                language
            );
        } else {
            List<String> ret = new ArrayList<>();
            HashMap<String, Integer> mapping = new LinkedHashMap<>();
            Number fractionalPart = util.Numbers.getFractionalPart(seconds);
            String result;

            seconds = seconds.longValue();

            mapping.put("year", 3600 * 24 * 365);
            mapping.put("month", 3600 * 24 * 30);
            mapping.put("day", 3600 * 24);
            mapping.put("hour", 3600);
            mapping.put("minute", 60);
            mapping.put("second", 1);

            for (Map.Entry<String, Integer> each : mapping.entrySet()) {
                long value = seconds.longValue() / each.getValue();
                Number valueToUse = 0;

                if (value > 0) {
                    if (!each.getKey().equals("second")) {
                        seconds = seconds.longValue() - each.getValue() * value;
                    }

                    if (each.getKey().equals("second")) {
                        if (util.Numbers.hasDecimals(fractionalPart)) {
                            valueToUse = value + fractionalPart.floatValue();
                        } else {
                            valueToUse = value;
                        }
                    } else {
                        valueToUse = value;
                    }
                } else if (each.getKey().equals("second")
                            && util.Numbers.hasDecimals(fractionalPart)) {
                    valueToUse = fractionalPart;
                }

                if (0 != valueToUse.floatValue()) {
                    ret.add(util.Localization.formatNumber(
                        valueToUse,
                        Language.get(each.getKey().concat("s").toUpperCase(), language),
                        Language.get(each.getKey().toUpperCase(), language),
                        language
                    ));
                }
            }

            if (ret.size() > 1) {
                String andWord = String.format(
                    " %s ",
                    Language.get("AND", language)
                );

                List<String> temp = new ArrayList<>();
                temp.add(util.Strings.implode(", ", ret.subList(0, ret.size() - 1)));
                temp.add(ret.get(ret.size() - 1));

                result = util.Strings.implode(
                    andWord,
                    temp
                );
            } else {
                result = ret.get(0);
            }

            return result;
        }
    }

    public static String formatSecondsToLongString(Number seconds, String language) {
        return formatSecondsToLongString(seconds, language, DEFAULT_RETURN_DECIMAL);
    }
}
