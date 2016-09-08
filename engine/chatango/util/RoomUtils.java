package engine.chatango.util;

import engine.chatango.common.Defaults;
import engine.chatango.common.RoomDefaults;

public class RoomUtils {
    /**
     * Get the server number for a room
     *
     * @return server number
     */
    public static int getServerNumber(String roomName) {
        roomName = roomName.toLowerCase();

        if (RoomDefaults.getDefaultServerNumbers().containsKey(roomName)) {
            return RoomDefaults.getDefaultServerNumbers().get(roomName);
        } else {
            String lnvStr;
            float fnv,
                  lnv,
                  number,
                  cumFrequency = 0;
            int serverNumber = 0,
                weightsSum = RoomDefaults.getWeightsSum();

            roomName = roomName.replaceAll("[_-]", "q");

            fnv = (float) Integer.parseInt(roomName.substring(0, Math.min(5, roomName.length())), 36);

            try {
                lnvStr = roomName.substring(6, 6 + Math.min(3, roomName.length() - 5));
                lnv = (float) Integer.parseInt(lnvStr, 36);
                if (lnv <= 1000) {
                    lnv = 1000;
                }
            } catch (Exception e) {
                lnv = 1000;
            }

            number = (fnv % lnv) / lnv;

            for (int[] each : RoomDefaults.SERVER_WEIGHTS) {
                cumFrequency += (float) each[1] / weightsSum;
                if (number <= cumFrequency) {
                    serverNumber = each[0];
                    break;
                }
            }

            return serverNumber;
        }
    }

    /**
     * Get the tag server for a certain room
     *
     * @param roomName the room name
     * @return the tag server
     */
    public static String getTagServer(String roomName) {
        return String.format(
                "%s%d.%s",
                RoomDefaults.TAG_SERVER_PREFIX,
                getServerNumber(roomName),
                Defaults.BASE_DOMAIN
        );
    }
}
