package engine.chatango.manager.EventManager;

import engine.chatango.common.Stream.Stream;

public interface EventManager extends EventManagerPM, EventManagerRoom {
    /**
     * Called before any command parsing occurs
     *
     * @param stream stream where the event was called from
     * @param raw raw data
     */
    default void onRaw(Stream stream, String raw) {}

    /**
     * Called on every event/
     *
     * @param stream stream where the event was called from
     * @param name event name
     * @param args other arguments
     */
    default void onEventCalled(Stream stream, String name, Object... args) {}

    // TODO
    default void onPremiumLow(String string) {}

    /**
     * The first event that is called.
     */
    default void onInit() {}

    /**
     * Called after the running flag has been set to false
     * and before the bot was disconnected from the streams.
     */
    default void onBeforeStop() {}

    /**
     * Called after the bot was disconnected from the streams.
     */
    default void onAfterStop() {}
}
