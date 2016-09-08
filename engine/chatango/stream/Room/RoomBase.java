package engine.chatango.stream.Room;

import common.Game;
import engine.chatango.common.RoomMessage;
import engine.chatango.common.Stream.Stream;
import engine.chatango.common.User;
import engine.chatango.manager.StreamManager.StreamManager;
import engine.chatango.util.Misc;

import java.util.*;

class RoomBase extends Stream {
    final protected static boolean USER_LIST_UNIQUE = true;
    final protected static int USER_LIST_MEMORY = 50;
    final protected static boolean USER_LIST_EVENT_UNIQUE = false;
    final protected static int MAX_HISTORY_LENGTH = 1000;
    final protected static int MAX_LENGTH = 2500;
    final protected static boolean BIG_MESSAGE_CUT = true;
    final protected static boolean BIG_MESSAGE_MULTIPLE = false;

    final private static int PORT = 443;

    protected String server;

    protected boolean reconnecting = false;
    protected String uniqueId;
    protected User owner;
    protected Set<User> moderators = new LinkedHashSet<>();
    protected Map<String, RoomMessage> messageQueue = new HashMap<>();
    protected List<RoomMessage> history = new ArrayList<>();
    protected List<User> userList = new ArrayList<>();
    protected int connectAmount = 0;
    protected int userCount = 0;
    protected Map<String, RoomMessage> messages = new HashMap<>();
    protected List<RoomMessage> iLog = new ArrayList<>();

    protected List<Map<String, Object>> banList = new ArrayList<>();
    protected List<Map<String, Object>> unbanList = new ArrayList<>();
    protected List<String> bannedWords = new ArrayList<>();
    protected List<String> recentList = new ArrayList<>();

    protected boolean sleeping = false;
    protected long flood = 0;
    protected String language;
    protected Game game;
    protected String mode;
    protected boolean locked = true;
    protected boolean home = false;
    protected boolean loginFail = false;
    protected int rateLimit = 0;
    protected boolean rateLimitFirst = true;
    protected int banCount = 0;
    protected long banTime = 0;

    public void connect() {
        firstCommand = true;
        writeBuffer.reset();

        try {
            createSocketChannel(server, PORT);

            if (auth()) {
                pingTask = manager.setInterval(
                        StreamManager.PING_DELAY,
                        this,
                        getClass().getMethod("ping")
                );
                if (!reconnecting) {
                    connected = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void disconnect(boolean reconnect) {
        if (reconnect && connectAmount > 0) {
            reconnect();
        } else {
            fireEvent("disconnect");
            if (!reconnecting) {
                connected = false;
            }
            for (User user : userList) {
                user.clearSessionIds((Room) this);
            }
            userList.clear();
            pingTask.cancel();
            try {
                socketChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!reconnecting) {
                manager.getRoomsMap().remove(name);
            }
        }
    }

    public void disconnect() {
        disconnect(DEFAULT_DISCONNECT_RECONNECT);
    }

    public void reconnect() {
        reconnecting = true;
        if (connected) {
            disconnect(false);
        }
        uniqueId = Misc.generateUniqueId();
        connect();
        reconnecting = false;
    }

    public boolean auth() {
        sendCommand("bauth", name, uniqueId, manager.getName(), manager.getPassword());
        setWriteLock(true);

        return true;
    }

    public void ping() {
        sendCommand("");
        fireEvent("ping");
    }
}
