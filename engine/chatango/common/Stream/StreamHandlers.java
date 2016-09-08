package engine.chatango.common.Stream;

import engine.chatango.common.User;
import util.Numbers;

abstract public class StreamHandlers extends StreamUtils {
    public void onReceivePremium(String[] args) {
        if (Numbers.secondsStringToMilliseconds(args[1]) > System.currentTimeMillis()) {
            premium = true;
            User loginUser = manager.getLoginUser();

            if (loginUser.hasMessageBackground()) {
                setBackgroundMode(true);
            }

            if (loginUser.hasMessageRecording()) {
                setRecordingMode(true);
            }
        } else {
            premium = false;
        }
    }
}
