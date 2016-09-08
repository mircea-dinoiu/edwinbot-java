package engine.chatango.manager.StreamManager;

import engine.chatango.common.Task;
import engine.chatango.common.User;
import engine.chatango.stream.Room.Room;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

abstract class StreamManagerUtils extends StreamManagerBase {
    private Map<String, User> users = new HashMap<>();

    public User getUser(String id) {
        if (users.containsKey(id.toLowerCase())) {
            return users.get(id.toLowerCase());
        } else {
            return new User(id);
        }
    }

    public User createUser(String id) {
        if (users.containsKey(id.toLowerCase())) {
            return users.get(id.toLowerCase());
        } else {
            User user = new User(id);
            users.put(id, user);
            return user;
        }
    }

    public User getLoginUser() {
        return createUser(name);
    }

    public String getLoginUserId() {
        return getLoginUser().getId();
    }

    /**
     * Call a method after at least timeout seconds with specified arguments.
     *
     * @param timeout timeout
     * @param invokeFrom the object that has the method as a member
     * @param method method to call
     * @param args arguments to use for method calling
     * @return the task created
     */
    public Task setTimeout(long timeout, Object invokeFrom, Method method, Object... args) {
        Task task = new Task();

        task.setManager((StreamManager) this);
        task.setInvokeFrom(invokeFrom);
        task.setTimeout(timeout);
        task.setMethod(method);
        task.setArgs(args);

        synchronized (tasks) {
            tasks.add(task);
        }

        return task;
    }

    /**
     * Call a method at least every timeout seconds with specified arguments.
     *
     * @param timeout timeout
     * @param invokeFrom the object that has the method as a member
     * @param method method to call
     * @param args arguments to use for method calling
     * @return the task created
     */
    public Task setInterval(long timeout, Object invokeFrom, Method method, Object... args) {
        Task task = new Task();

        task.setManager((StreamManager) this);
        task.setInvokeFrom(invokeFrom);
        task.setTimeout(timeout);
        task.setMethod(method);
        task.setArgs(args);
        task.setIsInterval(true);

        synchronized (tasks) {
            tasks.add(task);
        }

        return task;
    }

    /**
     * Cancels a task.
     *
     * @param task task to cancel
     */
    public void removeTask(Task task) {
        synchronized (tasks) {
            tasks.remove(task);
        }
    }

    /**
     * Joins a room
     *
     * @param roomName room's name
     */
    public void joinRoom(String roomName) {
        roomName = roomName.toLowerCase();

        if (!rooms.containsKey(roomName)) {
            Room room = new Room(roomName, (StreamManager) this);
            rooms.put(roomName, room);
        }
    }


    /**
     * Leaves a room
     *
     * @param roomName room's name
     */
    public void leaveRoom(String roomName) {
        Room room = getRoom(roomName);

        if (null != room) {
            System.out.println(String.format("%s: leaving", roomName.toLowerCase()));
            room.disconnect(false);
        }
    }

    /**
     * Get room by name
     *
     * @param roomName room's name
     * @return room
     */
    public Room getRoom(String roomName) {
        roomName = roomName.toLowerCase();

        if (rooms.containsKey(roomName)) {
            return rooms.get(roomName);
        } else {
            return null;
        }
    }

    /**
     * Enabled background if available.
     */
    public void enableBackground() {
        getLoginUser().setMessageBackground(true);

        for (Room room : rooms.values()) {
            room.setBackgroundMode(true);
        }
    }

    /**
     * Disable background.
     */
    public void disableBackground() {
        getLoginUser().setMessageBackground(false);

        for (Room room : rooms.values()) {
            room.setBackgroundMode(false);
        }
    }

    /**
     * Enable recording if available.
     */
    public void enableRecording() {
        getLoginUser().setMessageRecording(true);

        for (Room room : rooms.values()) {
            room.setRecordingMode(true);
        }
    }

    /**
     * Disable recording
     */
    public void disableRecording() {
        getLoginUser().setMessageRecording(false);

        for (Room room : rooms.values()) {
            room.setRecordingMode(false);
        }
    }

    /**
     * Set name color
     *
     * @param color a 3-char RGB hex code for the color
     */
    public void setNameColor(String color) {
        getLoginUser().setNameColor(color);
    }

    /**
     * Set font color
     *
     * @param color a 3-char RGB hex code for the color
     */
    public void setFontColor(String color) {
        getLoginUser().setFontColor(color);
    }

    /**
     * Set font face/family
     *
     * @param fontFace font face
     */
    public void setFontFace(String fontFace) {
        getLoginUser().setFontFace(fontFace);
    }

    /**
     * Set font size
     *
     * @param size the font size (between 9 and 22)
     */
    public void setFontSize(short size) {
        if (size < 9) {
            size = 9;
        }
        if (size > 22) {
            size = 22;
        }

        getLoginUser().setFontSize(size);
    }
}
