package engine.chatango.manager.EventManager;

import engine.chatango.common.User;
import engine.chatango.stream.PM.PM;

public interface EventManagerPM {
    default void onPMOK(PM pm) {}

    default void onPMConnect(PM pm, User user, boolean idle, String status) {}
    default void onPMDisconnect(PM pm) {}

    default void onPMPing(PM pm) {}

    default void onPMMessage(PM pm, User user, String messageBody) {}
    default void onPMOfflineMessage(PM pm, User user, String messageBody) {}

    default void onPMContactListReceive(PM pm) {}
    default void onPMBlockListReceive(PM pm) {}

    default void onPMContactAdd(PM pm, User user) {}
    default void onPMContactRemove(PM pm, User user) {}

    default void onPMBlock(PM pm, User user) {}
    default void onPMUnblock(PM pm, User user) {}

    default void onPMIdle(PM pm, boolean idle) {}
    default void onPMContactOnline(PM pm, User user) {}
    default void onPMContactOffline(PM pm, User user) {}
}
