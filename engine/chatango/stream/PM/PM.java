package engine.chatango.stream.PM;

import engine.chatango.common.User;
import engine.chatango.manager.StreamManager.StreamManager;

import java.util.Set;

public class PM extends PMHandlers {
    public PM(StreamManager manager) {
        this.manager = manager;
        this.name = "Private Messaging";

        connect();
    }

    public String getAuthId() {
        return authId;
    }

    public Set<User> getContacts() {
        return contacts;
    }

    public Set<User> getBlockList() {
        return blockList;
    }

    public Set<User> getUnblockList() {
        return unblockList;
    }
}
