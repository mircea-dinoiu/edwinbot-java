package engine.chatango.stream.PM;

import engine.chatango.common.User;
import util.HTML;
import util.Strings;

import java.util.Arrays;

public class PMHandlers extends PMUtils {
    public void onReceiveIdleupdate(String[] args) {

    }

    public void onReceiveTime(String[] args) {
    }

    public void onReceiveStatus(String[] args) {

    }

    public void onReceiveSellerName(String[] args) {

    }

    public void onReceiveOK() {
        setWriteLock(false);
        sendCommand("wl");
        sendCommand("getblock");
        sendCommand("getpremium", "1");
        setIdle();
        fireEvent("PMOK");
    }

    public void onReceiveWl(String[] args) {
        contacts.clear();

        for (int index = 0; index < Math.floorDiv(args.length, 4); index++) {
            String name = args[index * 4];
//            String lastOnline = args[index * 4 + 1];
//            String isOnline = args[index * 4 + 2];
//            String idle = args[index * 4 + 3];

            contacts.add(manager.createUser(name));
        }

        fireEvent("PMContactListReceive");
    }

    public void onReceiveBlockList(String[] args) {
        blockList.clear();

        for (String name : args) {
            if (!name.equals("")) {
                blockList.add(manager.createUser(name));
            }
        }
    }

    public void onReceiveUnblockList(String[] args) {
        unblockList.clear();

        for (String name : args) {
            if (!name.equals("")) {
                unblockList.add(manager.createUser(name));
            }
        }
    }

    public void onReceiveDenied() {
        disconnect();
        fireEvent("loginFail");
    }

    public void onReceiveMsg(String[] args) {
        User user = manager.createUser(args[0]);
        String body;

        // Clean message
        body = HTML.stripHTML(Strings.implode(":", Arrays.copyOfRange(args, 5, args.length)))
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&amp;", "&");

        fireEvent("PMMessage", user, body);
    }

    public void onReceiveMsgoff(String[] args) {
        User user = manager.createUser(args[0]);
        String body = HTML.stripHTML(Strings.implode(":", Arrays.copyOfRange(args, 5, args.length)));

        fireEvent("PMOfflineMessage", user, body);
    }

    public void onReceiveWlonline(String[] args) {
        fireEvent("PMContactOnline", manager.createUser(args[0]));
    }

    public void onReceiveWloffline(String[] args) {
        fireEvent("PMContactOffline", manager.createUser(args[0]));
    }

    public void onReceiveConnect(String[] args) {
        fireEvent("PMConnect", manager.createUser(args[0]), args[1], args[2]);
    }

    public void onReceiveKickingoff() {
        disconnect();
    }
}
