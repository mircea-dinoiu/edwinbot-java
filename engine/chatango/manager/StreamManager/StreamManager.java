package engine.chatango.manager.StreamManager;

import engine.chatango.stream.PM.PM;
import engine.chatango.stream.Room.Room;

import java.nio.channels.Selector;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract public class StreamManager extends StreamManagerUtils {
    final private static String CONSTRUCT_ERROR_MESSAGE = "StreamManager: Specify the correct parameters!";

    private static String getNameFromArgs(Object[] args) throws Exception {
        try {
            return (String) args[0];
        } catch (Exception e) {
            throw new Exception(CONSTRUCT_ERROR_MESSAGE);
        }
    }

    private static String getPasswordFromArgs(Object[] args) throws Exception {
        try {
            return (String) args[1];
        } catch (Exception e) {
            throw new Exception(CONSTRUCT_ERROR_MESSAGE);
        }
    }

    private static boolean getUsePMFromArgs(Object[] args) throws Exception {
        try {
            return (boolean) args[2];
        } catch (Exception e) {
            throw new Exception(CONSTRUCT_ERROR_MESSAGE);
        }
    }

    public StreamManager(Object... args) throws Exception {
        this(getNameFromArgs(args), getPasswordFromArgs(args), getUsePMFromArgs(args));
    }

    public StreamManager(String name, String password, boolean usePM) {
        // TODO param db ???
        this.name = name;
        this.password = password;

        start();

        if (usePM) {
            pm = new PM(this);
        }
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public List<Room> getRooms() {
        return (List<Room>) rooms.values();
    }

    public Map<String, Room> getRoomsMap() {
        return rooms;
    }

    public Set<String> getRoomNames() {
        return rooms.keySet();
    }

    public PM getPM() {
        return pm;
    }

    public Selector getSelector() {
        return selector;
    }
}
