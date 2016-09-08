package engine.chatango.common;

import engine.chatango.stream.Room.Room;

public class RoomMessage {
    private String id;
    private long time;
    private User user;
    private String body;
    private Room room;
    private String raw = "";
    private String IP;
    private String uniqueId = "";
    private String sharedId = "";
    private String nameColor = "000";
    private short fontSize = 12;
    private String fontFace = "0";
    private String fontColor = "000";

    public RoomMessage() {

    }

    public RoomMessage(String messageId) {
        id = messageId;
    }

    /**
     * Attach & detach
     */
    public void attach(Room room, String id) {
        if (null == this.id) {
            this.room = room;
            this.id = id;
            this.room.getMessages().put(id, this);
        }
    }

    public void detach() {
        if (null != id && null != room.getMessage(id)) {
            room.getMessages().remove(id);
            id = null;
        }
    }

    /**
     * Id
     */
    public String getId() {
        return id;
    }

    /**
     * Time
     */
    public long getTime() {
        return time;
    }

    public void setTime(long newTime) {
        time = newTime;
    }

    /**
     * User
     */
    public User getUser() {
        return user;
    }

    public void setUser(User newUser) {
        user = newUser;
    }

    /**
     * Body
     */
    public String getBody() {
        return body;
    }

    public void setBody(String newBody) {
        body = newBody;
    }

    /**
     * IP
     */
    public String getIP() {
        return IP;
    }

    public void setIP(String newIP) {
        IP = newIP;
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
     * Font face
     */
    public String getFontFace() {
        return fontFace;
    }

    public void setFontFace(String newFontFace) {
        fontFace = newFontFace;
    }

    /**
     * Font face
     */
    public short getFontSize() {
        return fontSize;
    }

    public void setFontSize(short newFontSize) {
        fontSize = newFontSize;
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
     * Unique id
     */
    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String newUniqueId) {
        uniqueId = newUniqueId;
    }

    /**
     * Room
     */
    public Room getRoom() {
        return room;
    }

    public void setRoom(Room newRoom) {
        room = newRoom;
    }

    /**
     * Raw
     */
    public String getRaw() {
        return raw;
    }

    public void setRaw(String newRaw) {
        raw = newRaw;
    }

    /**
     * Shared id
     */
    public String getSharedId() {
        return sharedId;
    }

    public void setSharedId(String newSharedId) {
        sharedId = newSharedId;
    }
}
