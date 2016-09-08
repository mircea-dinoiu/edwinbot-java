package engine.chatango.manager.EventManager;

import engine.chatango.common.RoomMessage;
import engine.chatango.common.User;
import engine.chatango.stream.Room.Room;

import java.util.List;

public interface EventManagerRoom {
    /**
     * Called when connected to a room.
     *
     * @param room room where the event was called from
     */
    default void onConnect(Room room) {}

    /**
     * Called when reconnected to a room.
     *
     * @param room room where the event was called from
     */
    default void onReconnect(Room room) {}

    /**
     * Called when the connection has failed.
     *
     * @param room room where the event was called from
     */
    default void onConnectFail(Room room) {}

    /**
     * Called when the client gets disconnected.
     *
     * @param room room where the event was called from
     */
    default void onDisconnect(Room room) {}

    /**
     * Called on login failure, disconnects after.
     *
     * @param room room where the event was called from
     */
    default void onLoginFail(Room room) {}

    /**
     * Called when flood banned or flagged.
     *
     * @param room room where the event was called from
     */
    default void onFloodBan(Room room) {}

    /**
     * Called when trying to send a message while flood banned or flagged.
     *
     * @param room room where the event was called from
     */
    default void onFloodBanRepeat(Room room) {}

    /**
     * Called when receive an overflow warning.
     *
     * @param room room where the event was called from
     */
    default void onFloodWarning(Room room) {}

    /**
     * Called when a message gets deleted.
     *
     * @param room room where the event was called from
     * @param user message's owner
     * @param message deleted message
     */
    default void onMessageDelete(Room room, User user, RoomMessage message) {}

    /**
     * Called when the chat owner clears all messages.
     *
     * @param room room where the event was called from
     */
    default void onClearAll(Room room) {}

    /**
     * Called when the moderator list is updated.
     *
     * @param room room where the event was called from
     */
    default void onModeratorListUpdate(Room room) {}

    /**
     * Called when there's a new moderator on the list.
     *
     * @param room room where the event was called from
     * @param user the new moderator
     */
    default void onModeratorAdd(Room room, User user) {}

    /**
     * Called when a moderator has been removed from the list.
     *
     * @param room room where the event was called from
     * @param user the removed moderator
     */
    default void onModeratorRemove(Room room, User user) {}

    /**
     * Called when a message has been received.
     *
     * @param room room where the event was called from
     * @param user message's owner
     * @param message received message
     */
    default void onMessage(Room room, User user, RoomMessage message) {}

    /**
     * Called when a room banned words list is received.
     *
     * @param room room where the event was called from
     * @param words list banned words
     */
    default void onBannedWordsListUpdate(Room room, List<String> words) {}

    /**
     * Called when a message gets received from history.
     *
     * @param room room where the event was called from
     * @param user message's owner
     * @param message received message
     */
    default void onHistoryMessage(Room room, User user, RoomMessage message) {}

    /**
     * Called when a user joins. Anonymous users are ignored.
     *
     * @param room room where the event was called from
     * @param user the user that has joined
     */
    default void onJoin(Room room, User user) {}

    /**
     * Called when a user leaves. Anonymous users are ignored.
     *
     * @param room room where the event was called from
     * @param user the user that has left
     */
    default void onLeave(Room room, User user) {}

    /**
     * Called when a ping is sent.
     *
     * @param room room where the event was called from
     */
    default void onPing(Room room) {}

    /**
     * Called when the user count changes.
     *
     * @param room room where the event was called from
     */
    default void onUserCountChange(Room room) {}

    /**
     * Called when a user is banned.
     *
     * @param room room where the event was called from
     * @param moderator the moderator that has banned the user
     * @param bannedUser the banned user
     */
    default void onBan(Room room, User moderator, User bannedUser) {}

    /**
     * Called when a user is unbanned.
     *
     * @param room room where the event was called from
     * @param moderator the moderator that has banned the user
     * @param unbannedUser the user that has been unbanned
     */
    default void onUnban(Room room, User moderator, User unbannedUser) {}

    /**
     * Called when the ban list is updated.
     *
     * @param room room where the event was called from
     */
    default void onBanListUpdate(Room room) {}

    /**
     * Called when the unban list is updated.
     *
     * @param room room where the event was called from
     */
    default void onUnbanListUpdate(Room room) {}
}
