package engine.chatango.common.Stream;

import java.io.IOException;
import java.util.Iterator;

abstract class StreamUtils extends StreamBase {
    protected void recalculateWaitUntil() {
        waitUntil = System.currentTimeMillis() + delay;
    }

    protected void lookIntoDelayedCommands() {
        synchronized (delayedCommands) {
            Iterator<String> iterator = delayedCommands.iterator();

            while (iterator.hasNext()) {
                if (System.currentTimeMillis() >= waitUntil) {
                    String command = iterator.next();

                    try {
                        write(command);
                        iterator.remove();

                        recalculateWaitUntil();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }
    }

    public void setBackgroundMode(boolean mode) {
        sendCommand("msgbg", mode ? "1": "0");
    }

    public void setRecordingMode(boolean mode) {
        sendCommand("msgmedia", mode ? "1" : "0");
    }
}
