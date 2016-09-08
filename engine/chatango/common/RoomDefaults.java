package engine.chatango.common;

import java.util.HashMap;
import java.util.Map;

public class RoomDefaults {
    final public static String TAG_SERVER_PREFIX = "s";

    final public static int WEIGHT_12 = 75;
    final public static int SERVER_2 = 95;
    final public static int SERVER_4 = 110;
    final public static int SERVER_6 = 104;
    final public static int SERVER_8 = 101;
    final public static int SERVER_10 = 110;
    final public static int SERVER_12 = 116;

    private static int SERVER_WEIGHTS_SUM = 0;

    final private static Map<String, Integer> DEFAULT_SERVER_NUMBERS = new HashMap<>();

    final public static int[][] SERVER_WEIGHTS = {
            {5,  WEIGHT_12},
            {6,  WEIGHT_12},
            {7,  WEIGHT_12},
            {8,  WEIGHT_12},
            {16, WEIGHT_12},
            {17, WEIGHT_12},
            {18, WEIGHT_12},
            {9,  SERVER_2},
            {11, SERVER_2},
            {12, SERVER_2},
            {13, SERVER_2},
            {14, SERVER_2},
            {15, SERVER_2},
            {19, SERVER_4},
            {23, SERVER_4},
            {24, SERVER_4},
            {25, SERVER_4},
            {26, SERVER_4},
            {28, SERVER_6},
            {29, SERVER_6},
            {30, SERVER_6},
            {31, SERVER_6},
            {32, SERVER_6},
            {33, SERVER_6},
            {35, SERVER_8},
            {36, SERVER_8},
            {37, SERVER_8},
            {38, SERVER_8},
            {39, SERVER_8},
            {40, SERVER_8},
            {41, SERVER_8},
            {42, SERVER_8},
            {43, SERVER_8},
            {44, SERVER_8},
            {45, SERVER_8},
            {46, SERVER_8},
            {47, SERVER_8},
            {48, SERVER_8},
            {49, SERVER_8},
            {50, SERVER_8},
            {52, SERVER_10},
            {53, SERVER_10},
            {55, SERVER_10},
            {57, SERVER_10},
            {58, SERVER_10},
            {59, SERVER_10},
            {60, SERVER_10},
            {61, SERVER_10},
            {62, SERVER_10},
            {63, SERVER_10},
            {64, SERVER_10},
            {65, SERVER_10},
            {66, SERVER_10},
            {68, SERVER_2},
            {71, SERVER_12},
            {72, SERVER_12},
            {73, SERVER_12},
            {74, SERVER_12},
            {75, SERVER_12},
            {76, SERVER_12},
            {77, SERVER_12},
            {78, SERVER_12},
            {79, SERVER_12},
            {80, SERVER_12},
            {81, SERVER_12},
            {82, SERVER_12},
            {83, SERVER_12},
            {84, SERVER_12}
    };

    /**
     * Lazy loading the default server numbers
     */
    private static void initDefaultServerNumbers() {
        if (DEFAULT_SERVER_NUMBERS.isEmpty()) {
            DEFAULT_SERVER_NUMBERS.put("de-livechat", 5);
            DEFAULT_SERVER_NUMBERS.put("ver-anime", 8);
            DEFAULT_SERVER_NUMBERS.put("watch-dragonball", 8);
            DEFAULT_SERVER_NUMBERS.put("narutowire", 10);
            DEFAULT_SERVER_NUMBERS.put("dbzepisodeorg", 10);
            DEFAULT_SERVER_NUMBERS.put("animelinkz", 20);
            DEFAULT_SERVER_NUMBERS.put("kiiiikiii", 21);
            DEFAULT_SERVER_NUMBERS.put("soccerjumbo", 21);
            DEFAULT_SERVER_NUMBERS.put("vipstand", 21);
            DEFAULT_SERVER_NUMBERS.put("cricket365live", 21);
            DEFAULT_SERVER_NUMBERS.put("pokemonepisodeorg", 22);
            DEFAULT_SERVER_NUMBERS.put("watchanimeonn", 22);
            DEFAULT_SERVER_NUMBERS.put("leeplarp", 27);
            DEFAULT_SERVER_NUMBERS.put("animeultimacom", 34);
            DEFAULT_SERVER_NUMBERS.put("rgsmotrisport", 51);
            DEFAULT_SERVER_NUMBERS.put("cricvid-hitcric-", 51);
            DEFAULT_SERVER_NUMBERS.put("ronaldo7-net", 51);
            DEFAULT_SERVER_NUMBERS.put("darksouls2wiki", 54);
            DEFAULT_SERVER_NUMBERS.put("stream2watch3", 56);
            DEFAULT_SERVER_NUMBERS.put("mitvcanal", 56);
            DEFAULT_SERVER_NUMBERS.put("sport24lt", 56);
            DEFAULT_SERVER_NUMBERS.put("ttvsports", 56);
            DEFAULT_SERVER_NUMBERS.put("eafangames", 56);
            DEFAULT_SERVER_NUMBERS.put("myfoxdfw", 67);
            DEFAULT_SERVER_NUMBERS.put("peliculas-flv", 69);
            DEFAULT_SERVER_NUMBERS.put("narutochatt", 70);
        }
    }

    /**
     * Getter method for default server numbers
     *
     * @return default server numbers
     */
    public static Map<String, Integer> getDefaultServerNumbers() {
        initDefaultServerNumbers();

        return DEFAULT_SERVER_NUMBERS;
    }

    /**
     * Get the sum of the weights.
     * Calculated once and for all.
     *
     * @return sum of the weights
     */
    public static int getWeightsSum() {
        if (SERVER_WEIGHTS_SUM == 0) {
            for (int[] each : SERVER_WEIGHTS) {
                SERVER_WEIGHTS_SUM += each[1];
            }
        }

        return SERVER_WEIGHTS_SUM;
    }

}
