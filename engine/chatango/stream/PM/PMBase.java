package engine.chatango.stream.PM;

import engine.chatango.common.Stream.Stream;
import engine.chatango.common.User;
import engine.chatango.manager.StreamManager.StreamManager;
import engine.chatango.util.Misc;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

class PMBase extends Stream {
    final private static String HOST = "c1.chatango.com";
    final private static int PORT = 5222;

    protected boolean idle = true;
    protected Set<User> blockList = new LinkedHashSet<>();
    protected Set<User> unblockList = new LinkedHashSet<>();
    protected Set<User> contacts = new LinkedHashSet<>();
    protected long delay = 1100;

    public void connect() {
        firstCommand = true;
        writeBuffer.reset();

        try {
            createSocketChannel(HOST, PORT);

            if (auth()) {
                pingTask = manager.setInterval(
                        StreamManager.PING_DELAY,
                        this,
                        getClass().getMethod("ping")
                );
                connected = true;
            }
        } catch (IOException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(boolean reconnect) {
        fireEvent("PMDisconnect");
        connected = false;

        try {
            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        socketChannel = null;
        if (reconnect) {
            connect();
        }
    }

    public void disconnect() {
        disconnect(DEFAULT_DISCONNECT_RECONNECT);
    }

    public boolean auth() {
        authId = Misc.getAuthId(manager.getName(), manager.getPassword());

        if (null == authId) {
            try {
                socketChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            fireEvent("loginFail");
            socketChannel = null;
            return false;
        } else {
            sendCommand("tlogin", authId, "2");
            setWriteLock(true);
            return true;
        }
    }

    public void ping() {
        sendCommand("");
        fireEvent("PMPing");
        if (idle) {
            setIdle();
            idle = false;
        }
    }

    protected void setIdle() {
        sendCommand("idle", "0");
    }

    protected void setActive() {
        sendCommand("idle", "1");
    }
}
