package engine.chatango.common.Stream;

import engine.chatango.manager.StreamManager.StreamManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

abstract public class Stream extends StreamHandlers {
    public String getName() {
        return name;
    }

    public StreamManager getManager() {
        return manager;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean getPremium() {
        return premium;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public ByteArrayOutputStream getWriteBuffer() {
        return writeBuffer;
    }

    public void sliceWriteBuffer(int size) {
        if (writeBuffer.size() >= size) {
            byte[] bytes,
                   originalArray = writeBuffer.toByteArray();

            bytes = Arrays.copyOfRange(originalArray, size, originalArray.length);
            writeBuffer.reset();
            try {
                writeBuffer.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if there is something to write in the buffer.
     * Before that, goes through delayed commands and writes
     * them to buffer if the wait until unix timestamp is below the
     * current unix timestamp.
     *
     * @return true if there is something to write on the buffer
     */
    public boolean waitsForWriting() {
        lookIntoDelayedCommands();

        return writeBuffer.size() > 0;
    }
}
