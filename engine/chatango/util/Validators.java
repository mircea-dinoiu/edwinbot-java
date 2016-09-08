package engine.chatango.util;

public class Validators {
    final private static int MAX_ROOM_NAME_LENGTH = 20;
    final private static int MAX_USERNAME_LENGTH = 20;

    /**
     * Checks if a username is valid according to Chatango restrictions
     *
     * @param username username to check
     * @return true if username is valid, false otherwise
     */
    public static boolean usernameIsValid(String username) {
        boolean valid = false;
        String toCheck = username;

        toCheck = toCheck.toLowerCase().trim();

        if (toCheck.length() > 0) {
            String good = toCheck.replaceAll("[^a-z0-9#!]", "");

            if (good.equals(toCheck) && good.length() <= MAX_USERNAME_LENGTH) {
                valid = true;
            }
        }

        return valid;
    }

    /**
     * Checks if a room name is valid according to Chatango restrictions
     *
     * @param roomName room name to check
     * @return true if the room name is valid, false otherwise
     */
    public static boolean roomNameIsValid(String roomName) {
        boolean valid = false;
        String toCheck = roomName;

        toCheck = toCheck.toLowerCase().trim();

        if (toCheck.length() > 0) {
            String good = toCheck.replaceAll("[^a-z0-9-_]", "");

            if (good.equals(toCheck) && good.length() <= MAX_ROOM_NAME_LENGTH) {
                valid = true;
            }
        }

        return valid;
    }
}
