package util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.Random;

public class Numbers {
    /**
     * Converts a string number that represents seconds and milliseconds (floating point)
     * to a long number that represents the milliseconds.
     *
     * @param seconds seconds
     * @return milliseconds
     */
    public static long secondsStringToMilliseconds(String seconds) {
        String[] numberParts = String.valueOf(seconds).split("[.]");
        long ms;

        if (numberParts.length == 1) {
            ms = Long.valueOf(numberParts[0]) * 1000;
        } else {
            ms = Long.valueOf(numberParts[0] + StringUtils.rightPad(numberParts[1], 4, "0").substring(0, 4));
        }

        return ms;
    }

    /**
     * Get the fractional part of a number as whole number
     * Supports negative numbers
     *
     * @param number number to get the fractional part for
     * @return fractional part
     */
    public static Number getFractionalPartAsWhole(Number number) {
        if (hasDecimals(number)) {
            String[] numberParts = String.valueOf(number).split("[.]");
            return Long.valueOf(numberParts[1]);
        } else {
            return 0;
        }
    }

    /**
     * Get fractional part as double number
     * Supports negative numbers
     *
     * @param number number to get the fractional part for
     * @return fractional part
     */
    public static Number getFractionalPart(Number number) {
        if (hasDecimals(number)) {
            Number integerFractionalPart = getFractionalPartAsWhole(number);
            String stringFractionalPart = String.format(
                "0.%s",
                String.valueOf(integerFractionalPart)
            );
            return Float.valueOf(stringFractionalPart);
        } else {
            return 0;
        }
    }

    /**
     * Has decimals
     * Verifies if a number has no null decimals
     * Supports negative numbers
     *
     * @param number the number to check
     * @return true if the number has decimals, false otherwise
     */
    public static boolean hasDecimals(Number number) {
        return (0 != number.floatValue() % 1);
    }

    /**
     * Random range
     *
     * @param min minimum value (inclusive)
     * @param max maximum value (inclusive)
     * @return a random value
     */
    public static long randomRange(long min, long max) {
        Random random = new Random();
        long value = random.nextLong();

        while (value < min || value > max) {
            value = random.nextLong();
        }

        return value;
    }

    public static long randomRange(BigInteger min, BigInteger max) {
        return randomRange(min.longValue(), max.longValue());
    }

    public static int randomRange(int min, int max) {
        Random random = new Random();
        int value = random.nextInt(max + 1);

        while (value < min) {
            value = random.nextInt(max + 1);
        }

        return value;
    }

    public static double randomRange(double min, double max) {
        Random random = new Random();
        double value = random.nextDouble();

        while (value < min || value > max) {
            value = random.nextDouble();
        }

        return value;
    }

    /**
     * Percentage
     *
     * @param part how much
     * @param whole whole
     * @return percentage
     */
    public static double percentage(double part, double whole) {
        return (part * whole) / 100;
    }

    public static double percentage(double part, long whole) {
        return percentage(part, (double) whole);
    }

    /**
     * Get special number
     *
     * Cleaning a string by removing commas, spaces and dots
     * Use "m" as final character to specify that this number represents millions
     * Use "k" as final character to specify that this number represents thousands
     * Use "all" to return the specified reference
     *
     * @param numberString the special number
     * @return a long number
     */
    public static long specialNumber(String numberString) {
        numberString = numberString.toLowerCase().replace(",", "").replace(" ", "").replace(".", "");

        if (numberString.endsWith("k")) {
            numberString = numberString.substring(0, numberString.length() - 2).concat("000");
        } else if (numberString.endsWith("m")) {
            numberString = numberString.substring(0, numberString.length() - 2).concat("000000");
        }

        return Long.parseLong(numberString);
    }

    public static long specialNumber(String numberString, long reference) {
        numberString = numberString.toLowerCase().replace(",", "").replace(" ", "").replace(".", "");

        if (numberString.equals("all")) {
            return reference;
        } else {
            return specialNumber(numberString);
        }
    }
}
