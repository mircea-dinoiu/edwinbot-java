package engine.chatango.common.Stream;

import engine.chatango.common.Task;
import engine.chatango.manager.StreamManager.StreamManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import util.Strings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

abstract class StreamBase {
    final protected static boolean DEFAULT_DISCONNECT_RECONNECT = true;

    protected StreamManager manager;
    protected String name;
    protected SocketChannel socketChannel;
    protected boolean connected = false;
    protected String authId;

    protected boolean writeLock = false;
    protected final List<String> delayedCommands = new ArrayList<>();
    protected ByteArrayOutputStream writeBuffer = new ByteArrayOutputStream();
    protected ByteArrayOutputStream writeLockBuffer = new ByteArrayOutputStream();
    protected List<Byte> readBuffer = new ArrayList<>();

    protected boolean firstCommand = true;
    protected boolean premium = false;
    protected long waitUntil = 0;
    protected Task pingTask;
    protected long delay = 0;

    /**
     * Creates a non-blocking socket channel for the specified host name and port.
     *
     * @param hostName host name
     * @param port port
     * @throws IOException
     */
    protected void createSocketChannel(String hostName, int port) throws IOException {
        socketChannel = SocketChannel.open();

        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(hostName, port));
        socketChannel.register(manager.getSelector(), socketChannel.validOps());
    }

    /**
     * Connect to server
     */
    abstract public void connect();

    /**
     * Disconnect from the server
     *
     * @param reconnect false to avoid automatically reconnecting
     */
    abstract public void disconnect(boolean reconnect);

    abstract public void disconnect();

    /**
     * Authenticate
     */
    abstract public boolean auth();

    /**
     * Ping
     */
    abstract public void ping();

    public void feed(ByteBuffer buffer, int size) {
        for (int position = 0; position < size; position++) {
            if (0 == buffer.get(position)) {
                process(StringUtils.strip(Strings.byteListToString(readBuffer), "\r\n"));
                readBuffer.clear();
            } else {
                readBuffer.add(buffer.get(position));
            }
        }
    }

    public void process(String data) {
        if (data.length() > 0) {
            String[] dataArray,
                     args = {};
            StringBuffer command = new StringBuffer();
            String methodName;
            Method method;

            fireEvent("raw", data);

            dataArray = data.split("[:]", -1);
            for (String each : Strings.explode(dataArray[0], "_")) {
                command.append(WordUtils.capitalize(each));
            }
            methodName = String.format("onReceive%s", command);

            if (dataArray.length > 1) {
                args = ArrayUtils.subarray(dataArray, 1, dataArray.length);
            }

            try {
                if (dataArray.length > 1) {
                    method = getClass().getMethod(methodName, String[].class);
                    method.invoke(this, new Object[] {args});
                } else {
                    method = getClass().getMethod(methodName);
                    method.invoke(this);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                System.out.println(String.format("%s: There is a command that is not handled: %s", name, data));
            }
        }
    }

    protected void write(String data) throws IOException {
        if (writeLock) {
            writeLockBuffer.write(data.getBytes());
            writeLockBuffer.write(0x00);
        } else {
            writeBuffer.write(data.getBytes());
            writeBuffer.write(0x00);
        }
    }

    protected void fireEvent(String event, Object... args) {
        Object[] defaultArgs = {this};
        manager.fireEvent(event, ArrayUtils.addAll(defaultArgs, args));
    }

    protected void setWriteLock(boolean lock) {
        writeLock = lock;

        if (!writeLock) {
            try {
                writeBuffer.write(writeLockBuffer.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            writeLockBuffer.reset();
        }
    }

    /**
     * Sends a command.
     *
     * @param args list of arguments (the first argument should be the command name)
     */
    protected void sendCommand(String... args) {
        try {
            if (firstCommand) {
                firstCommand = false;
                write(args.length > 1 ? Strings.implode(":", args) : args[0]);
            } else {
                write((args.length > 1 ? Strings.implode(":", args) : args[0]) + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a delayed command.
     *
     * @param args list of arguments (the first argument should be the command name)
     */
    protected void sendDelayedCommand(String... args) {
        synchronized (delayedCommands) {
            if (firstCommand) {
                firstCommand = false;
                delayedCommands.add(Strings.implode(":", args));
            } else {
                delayedCommands.add(Strings.implode(":", args) + "\r\n");
            }
        }
    }
}