package engine.chatango.stream.Room;

import common.Game;
import engine.chatango.common.RoomMessage;
import engine.chatango.common.User;
import engine.chatango.manager.StreamManager.StreamManager;
import util.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Room extends RoomHandlers {
    final private static String DEFAULT_GET_USER_LIST_MODE = "registered";

    public Room(String roomName, StreamManager streamManager) {
        name = roomName;
        manager = streamManager;
        server = engine.chatango.util.RoomUtils.getTagServer(roomName);
        uniqueId = engine.chatango.util.Misc.generateUniqueId();

        connect();
    }

    /**
     * Recent user list
     */
    public List<User> getRecentUserList(int memory) {
        List<User> ul = new ArrayList<>();

        for (int index = history.size() - memory; index < history.size(); index++) {
            ul.add(history.get(index).getUser());
        }

        return ul;
    }

    public List<User> getRecentUserList() {
        return getRecentUserList(USER_LIST_MEMORY);
    }

    /**
     * User list
     */
    public List<User> getUserList(String mode, boolean unique, int memory) {
        List<User> ul = new ArrayList<>();

        switch (mode) {
            case "recent":
                ul.addAll(getRecentUserList(memory));
                break;
            case "active":
                for (User user : getRecentUserList(memory)) {
                    if (userList.contains(user) && !ul.contains(user)) {
                        ul.add(user);
                    }
                }
                unique = false;
                break;
            case "all":
                ul.addAll(getRecentUserList(memory));
                ul.addAll(userList);
                break;
            default:  // registered
                ul.addAll(userList);
                break;
        }

        if (unique) {
            Lists.removeDuplicates(ul);
        }

        return ul;
    }

    public List<User> getUserList(String mode, boolean unique) {
        return getUserList(mode, unique, USER_LIST_MEMORY);
    }

    public List<User> getUserList(String mode) {
        return getUserList(mode, USER_LIST_UNIQUE);
    }

    public List<User> getUserList() {
        return getUserList(DEFAULT_GET_USER_LIST_MODE);
    }

    /**
     * User ids
     */
    public String[] getUserIds() {
        String[] userIds = new String[userList.size()];
        int index = 0;

        for (User user : userList) {
            userIds[index] = user.getId();
            index++;
        }

        return userIds;
    }

    /**
     * User names
     */
    public String[] getUserNames() {
        String[] userIds = new String[userList.size()];
        int index = 0;

        for (User user : userList) {
            userIds[index] = user.getName();
            index++;
        }

        return userIds;
    }

    /**
     * Recent list
     */
    public List<String> getRecentList() {
        return recentList;
    }

    /**
     * Login user
     */
    public User getLoginUser() {
        return manager.getLoginUser();
    }

    /**
     * Owner
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Owner name
     */
    public String getOwnerName() {
        return owner.getName();
    }

    /**
     * Moderators
     */
    public Set<User> getModerators() {
        return moderators;
    }

    /**
     * Moderator ids
     */
    public String[] getModeratorIds() {
        String[] moderatorIds = new String[moderators.size()];
        int index = 0;

        for (User moderator : moderators) {
            moderatorIds[index] = moderator.getId();
        }

        return moderatorIds;
    }

    /**
     * Moderator names
     */
    public String[] getModeratorNames() {
        String[] moderatorIds = new String[moderators.size()];
        int index = 0;

        for (User moderator : moderators) {
            moderatorIds[index] = moderator.getName();
        }

        return moderatorIds;
    }

    /**
     * User count
     */
    public int getUserCount() {
        return userCount;
    }

    /**
     * Ban list
     */
    public User[] getBanList() {
        User[] list = new User[banList.size()];
        int index = 0;

        for (Map<String, Object> entry : banList) {
            list[index] = (User) entry.get("target");
            index++;
        }

        return list;
    }

    /**
     * Unban list
     */
    public User[] getUnbanList() {
        User[] list = new User[unbanList.size()];
        int index = 0;

        for (Map<String, Object> entry : unbanList) {
            list[index] = (User) entry.get("target");
            index++;
        }

        return list;
    }

    /**
     * Banned words
     */
    public List<String> getBannedWords() {
        return bannedWords;
    }

    /**
     * History
     */
    public List<RoomMessage> getHistory() {
        return history;
    }

    /**
     * Get room messages
     *
     * @return messages
     */
    public Map<String, RoomMessage> getMessages() {
        return messages;
    }

    /**
     * Sleep
     */
    public boolean isSleeping() {
        return sleeping;
    }

    public void setSleeping(boolean sleeping) {
        this.sleeping = sleeping;
    }

    /**
     * Language
     */
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Game
     */
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Mode
     */
    public String getMode() {
        return mode;
    }

    public void setMode(String newMode) {
        mode = newMode;
    }

    /**
     * Lock
     */
    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean newLocked) {
        locked = newLocked;
    }

    /**
     * Home
     */
    public boolean isHome() {
        return home;
    }

    public void setHome(boolean home) {
        this.home = home;
    }

    /**
     * Flood
     */
    public long getFlood() {
        return flood;
    }

    public void setFlood(long newFlood) {
        flood = newFlood;
    }

    /**
     * Login fail
     */
    public boolean getLoginFail() {
        return loginFail;
    }

    public void setLoginFail(boolean loginFail) {
        this.loginFail = loginFail;
    }

    /**
     * Delay
     */
    public long getDelay() {
        return delay;
    }

    public void setDelay(long newDelay) {
        delay = newDelay;
    }
}
