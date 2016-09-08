package engine.chatango.common;

import engine.chatango.stream.Room.Room;
import lib.CleverBot;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class User {
    private String id;
    private String name;
    private String nick;

    private String language;

    private Map<Room, HashSet<String>> sessionIds = new HashMap<>();

    private String nameColor = "000";
    private short fontSize = 12;
    private String fontFace = "0";
    private String fontColor = "000";

    private boolean messageBackground = false;
    private boolean messageRecording = false;

    private CleverBot cleverBot;

    public User(String userId) {
        id = userId.toLowerCase();
        name = WordUtils.capitalize(userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Id
     */
    public String getId() {
        return id;
    }

    /**
     * Name
     */
    public String getName() {
        return name;
    }

    /**
     * Nick
     */
    public String getNick() {
        return nick;
    }

    public void setNick(String newNick) {
        nick = newNick;
    }

    /**
     * Language
     */
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String newLanguage) {
        language = newLanguage;
    }

    /**
     * Session ids
     */
    public Set<String> getSessionIds(Room room) {
        if (null != room) {
            if (sessionIds.containsKey(room)) {
                return sessionIds.get(room);
            } else {
                return new HashSet<>();
            }
        } else {
            Set<String> allSessionIds = new HashSet<>();

            for (Set<String> eachSessionIds : sessionIds.values()) {
                allSessionIds.addAll(eachSessionIds);
            }

            return allSessionIds;
        }
    }

    public Set<String> getSessionIds() {
        return getSessionIds(null);
    }

    /**
     * Rooms
     */
    public Set<Room> getRooms() {
        return sessionIds.keySet();
    }

    /**
     * Room names
     */
    public String[] getRoomNames() {
        Set<Room> rooms = getRooms();
        String[] roomNames = new String[rooms.size()];
        int index = 0;

        for (Room room : rooms) {
            roomNames[index] = room.getName();
            index++;
        }

        return roomNames;
    }

    /**
     * Name color
     */
    public String getNameColor() {
        return nameColor;
    }

    public void setNameColor(String newNameColor) {
        nameColor = newNameColor;
    }

    /**
     * Font size
     */
    public short getFontSize() {
        return fontSize;
    }

    public String getFontSizeForMessage() {
        return StringUtils.leftPad(String.valueOf(fontSize), 2, "0");
    }

    public void setFontSize(short newFontSize) {
        fontSize = newFontSize;
    }

    /**
     * Font face
     */
    public String getFontFace() {
        return fontFace;
    }

    public void setFontFace(String newFontFace) {
        fontFace = newFontFace;
    }

    /**
     * Font color
     */
    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String newFontColor) {
        fontColor = newFontColor;
    }

    /**
     * Message background
     */
    public boolean hasMessageBackground() {
        return messageBackground;
    }

    public void setMessageBackground(boolean newMessageBackground) {
        messageBackground = newMessageBackground;
    }

    /**
     * Message recording
     */
    public boolean hasMessageRecording() {
        return messageRecording;
    }

    public void setMessageRecording(boolean newMessageRecording) {
        messageRecording = newMessageRecording;
    }

    /**
     * CleverBot
     */
    public CleverBot getCleverBot() {
        if (null == cleverBot) {
            cleverBot = new CleverBot();
        }

        return cleverBot;
    }

    /**
     * Util methods
     */
    public void addSessionId(Room room, String sessionId) {
        if (!sessionIds.containsKey(room)) {
            sessionIds.put(room, new HashSet<>());
            sessionIds.get(room).add(sessionId);
        }
    }

    public void removeSessionId(Room room, String sessionId) {
        try {
            sessionIds.get(room).remove(sessionId);
            if (sessionIds.get(room).isEmpty()) {
                clearSessionIds(room);
            }
        } catch (Exception e) {
            assert true;
        }
    }

    public void clearSessionIds(Room room) {
        try {
            sessionIds.remove(room);
        } catch (Exception e) {
            assert true;
        }
    }

    public boolean hasSessionId(Room room, String sessionId) {
        try {
            return sessionIds.get(room).contains(sessionId);
        } catch (Exception e) {
            return false;
        }
    }
}
