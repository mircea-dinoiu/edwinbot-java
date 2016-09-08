package engine.chatango.manager.StreamManager;

import engine.chatango.common.Stream.Stream;
import engine.chatango.common.Task;
import engine.chatango.stream.PM.PM;
import engine.chatango.stream.Room.Room;
import org.apache.commons.lang3.text.WordUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

abstract class StreamManagerBase {
    final private static long TIMER_RESOLUTION = 200;
    final public static long PING_DELAY = 20;

    protected String name;
    protected String password;
    protected volatile boolean running;
    protected final Set<Task> tasks = new HashSet<>();
    protected Map<String, Room> rooms = new HashMap<>();
    protected PM pm;
    protected Selector selector;

    private boolean selectedOnce = false;
    private ByteBuffer data = ByteBuffer.allocateDirect(1024);

    /**
     * Get method by name
     *
     * @param methodName method name to search
     * @return the method object or null if not found
     */
    private Method getMethodByName(String methodName) {
        for (Method method : getClass().getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }

        return null;
    }

    /**
     * Fires an event.
     *
     * @param name event name
     * @param args event arguments
     */
    public void fireEvent(String name, Object... args) {
        String methodName = String.format("on%s", WordUtils.capitalize(name));
        Method method = getMethodByName(methodName);
        Stream stream = null;

        if (args.length > 0 && args[0] instanceof Stream) {
            stream = (Stream) args[0];
        }

        try {
            method.invoke(this, args);
        } catch (Exception e) {
            System.out.println(String.format("EventManager: Event method %s is not implemented", methodName));
        }

        if (!name.equals("eventCalled")) {
            fireEvent("eventCalled", stream, name, args);
        }
    }

    /**
     * Run the stream manager.
     */
    protected void start() {
        running = true;

        try {
            selector = Selector.open();
            fireEvent("init");
        } catch (IOException e) {
            stop();
        }

        new Thread() {
            @Override
            public void run() {
                handleSocketChannels();
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                tick();
            }
        }.start();
    }

    /**
     * Get the stream that has a socket channel.
     *
     * @param socketChannel socket channel
     * @return the stream corresponding to the socket channel or null
     * if the instantiation of the stream has not finished yet
     */
    private Stream getStreamForSocketChannel(SocketChannel socketChannel) {
        if (null != pm && pm.getSocketChannel().equals(socketChannel)) {
            return pm;
        } else {
            for (Room room : rooms.values()) {
                if (room.getSocketChannel().equals(socketChannel)) {
                    return room;
                }
            }
        }

        return null;
    }

    /**
     * Process a selection key
     *
     * @param selectionKey selection key to process
     * @throws IOException
     */
    private void processSelectionKey(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel;
        Stream stream;

        if (selectionKey.isValid()) {
            socketChannel = (SocketChannel) selectionKey.channel();
            stream = getStreamForSocketChannel(socketChannel);

            if (selectionKey.isConnectable()) {
                boolean success = socketChannel.finishConnect();

                if (!success) {
                    // Unregister the channel with this selector
                    selectionKey.cancel();
                }
            } else if (null != stream) {
                if (selectionKey.isReadable()) {
                    int numBytesRead = socketChannel.read(data);

                    if (numBytesRead <= 0) {
                        System.out.println(numBytesRead);
                        stream.disconnect();
                        return;
                    } else {
                        stream.feed(data, numBytesRead);
                        data.clear();
                    }
                }

                if (selectionKey.isWritable()
                        && stream.waitsForWriting()) {
                    int size;
                    ByteArrayOutputStream toSend = stream.getWriteBuffer();
                    ByteBuffer writeBuffer = ByteBuffer.wrap(toSend.toByteArray());

                    size = socketChannel.write(writeBuffer);
                    stream.sliceWriteBuffer(size);
                }
            }
        }
    }

    /**
     * Handle streams.
     */
    private void handleSocketChannels() {
        Iterator<SelectionKey> it;
        SelectionKey selectionKey;
        int numberOfSelectedKeys;

        while (running) {
            try {
                // Wait for an event
                numberOfSelectedKeys = selector.select(TIMER_RESOLUTION);
            } catch (Exception e) {
                // Handle error with selector
                e.printStackTrace();
                stop();
                break;
            }

            if (selectedOnce && 0 == numberOfSelectedKeys) {
                stop();
            } else {
                // Get list of selection keys with pending events
                it = selector.selectedKeys().iterator();

                while (it.hasNext()) {
                    selectedOnce = true;

                    // Get the selection key
                    selectionKey = it.next();

                    // Remove it from the list to indicate that is being processed
                    it.remove();

                    try {
                        processSelectionKey(selectionKey);
                    } catch (IOException e) {
                        // Handle error with channel and unregister
                        selectionKey.cancel();
                    }
                }
            }
        }
    }

    /**
     * Tick method that handles tasks.
     */
    private void tick() {
        long now;
        Task task;
        Iterator<Task> iterator;

        while (running) {
            synchronized (tasks) {
                now = System.currentTimeMillis();
                iterator = tasks.iterator();

                while (iterator.hasNext()) {
                    task = iterator.next();

                    if (task.getTarget() < now) {
                        try {
                            task.getMethod().invoke(task.getInvokeFrom(), task.getArgs());
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }

                        if (task.isInterval()) {
                            task.resetTimeout();
                        } else {
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }

    /**
     * This is public because it can be used by commands
     */
    public void stop() {
        // Set the running flag to false
        running = false;

        fireEvent("beforeStop");

        // Disconnect from chatrooms
        for (Room room : rooms.values()) {
            if (room.isConnected()) {
                room.disconnect(false);
            }
        }

        // Disconnect from PM
        if (null != pm && pm.isConnected()) {
            pm.disconnect(false);
        }

        fireEvent("afterStop");
    }


}