package engine.chatango.stream.Room;

import engine.chatango.common.RoomMessage;
import engine.chatango.common.User;
import util.Misc;
import util.Numbers;
import util.Strings;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class RoomHandlers extends RoomUtils {
    public void onReceiveOk(String[] args) {
        if (!args[2].equals("M")) {
            // Happens often when the bot name contains a word banned on the chatroom.
            // We still want to read the banned words from that chatroom or the moderator list
            // but the bot won't be shown on the user list of the specific chatroom.
            fireEvent("loginFail");
            // disconnect();
        }

        owner = manager.getUser(args[0]);
        uniqueId = args[1];
        authId = args[1].substring(4, 8);
        iLog.clear();

        for (String moderatorId : Strings.explode(args[6], ";")) {
            moderators.add(manager.getUser(moderatorId));
        }
    }

    public void onReceiveDenied() {
        disconnect();
        fireEvent("connectFail");
    }

    public void onReceiveInited() {
        sendCommand("g_participants", "start");
        sendCommand("getpremium", "1");
        sendCommand("getbannedwords");
        sendCommand("getratelimit");

        requestUnbanList();
        requestBanList();

        if (connectAmount == 0) {
            fireEvent("connect");

            ListIterator<RoomMessage> iterator = iLog.listIterator(iLog.size());
            while (iterator.hasPrevious()) {
                RoomMessage message = iterator.previous();
                User user = message.getUser();

                fireEvent("historyMessage", user, message);
                addHistory(message);
            }

            iLog.clear();
        } else {
            fireEvent("reconnect");
        }

        connectAmount++;
        setWriteLock(false);
    }

    public void onReceiveRatelimited() {
        if (rateLimitFirst) {
            rateLimitFirst = false;
        } else {
            System.out.println(String.format("Warning: Check your settings, a message was not sent on %s", name));
        }
    }

    public void onReceiveGetratelimit(String[] args) {
        rateLimit = Integer.parseInt(args[0]);
    }

    public void onReceiveRatelimitset(String[] args) {
        rateLimit = Integer.parseInt(args[0]);
    }

    public void onReceiveBw(String[] args) {
        String content = args[1].equals("") ? args[2] : args[1];
        List<String> words = new ArrayList<>();
        boolean error = false;

        try {
            words.addAll(Arrays.asList(URLDecoder.decode(content, "UTF-8").split("[,]", -1)));
        } catch (UnsupportedEncodingException e) {
            error = true;
            e.printStackTrace();
        }

        if (!error) {
            for (String each : words) {
                if (!bannedWords.contains(each)) {
                    if (each.length() > 0) {
                        bannedWords.add(each);
                    }
                }
            }

            for (String each : bannedWords) {
                if (!words.contains(each) && !(words.size() == 0 && words.get(0).equals(""))) {
                    bannedWords.remove(each);
                }
            }

            fireEvent("bannedWordsListUpdate", bannedWords);
        }
    }

    public void onReceiveUbw() {
        sendCommand("getbannedwords");
    }

    public void onReceiveMods(String[] args) {
        Set<User> freshModerators = new HashSet<>();

        for (String moderatorId : args) {
            User moderator = manager.getUser(moderatorId);
            freshModerators.add(moderator);

            if (!moderators.contains(moderator)) {
                moderators.add(moderator);
                fireEvent("moderatorAdd", moderator);
            }
        }

        for (User moderator : moderators) {
            if (!freshModerators.contains(moderator)) {
                moderators.remove(moderator);
                fireEvent("moderatorRemove", moderator);
            }
        }

        fireEvent("moderatorListChange");
    }

    public void onReceiveB(String[] args) {
        long time = Numbers.secondsStringToMilliseconds(args[0]);
        String name = args[1];
        String uniqueId = args[3];
        String sharedId = args[4];
        String i = args[5];
        String IP = args[6].equals("") ? null : args[6];

        String rawMessage = Strings.implode(":", Arrays.copyOfRange(args, 9, args.length));
        Map<String, String> messageData = Misc.getMessageData(rawMessage);
        String body = messageData.get("message");
        String nameData = messageData.get("nameData");
        String fontStyle = messageData.get("fontStyle");
        String nameColor = null;
        String fontColor = null;
        String fontFace = null;
        short fontSize = 0;

        RoomMessage message = new RoomMessage();

        if (name.equals("")) {
            name = String.format("#%s", args[2]);
            if (name.equals("#")) {
                name = String.format("!anon%s", engine.chatango.util.Misc.getAnonId(nameData, uniqueId));
            }
        } else {
            if (nameData != null && nameData.length() > 0) {
                nameColor = engine.chatango.util.Misc.parseNameColor(nameData);
            }
        }

        if (fontStyle != null && fontStyle.length() > 0) {
            Map<String, Object> fontData = engine.chatango.util.Misc.parseFontStyle(fontStyle);
            fontColor = (String) fontData.get("color");
            fontFace = (String) fontData.get("face");
            fontSize = (short) fontData.get("size");
        }

        message.setTime(time);
        message.setUser(manager.createUser(name));
        message.setBody(body);
        message.setRaw(rawMessage);
        message.setUniqueId(uniqueId);
        message.setIP(IP);
        message.setNameColor(nameColor);
        message.setFontColor(fontColor);
        message.setFontFace(fontFace);
        message.setFontSize(fontSize);
        message.setSharedId(sharedId);
        message.setRoom((Room) this);

        messageQueue.put(i, message);

//        fireEvent("message", message.getUser(), message);
    }

    public void onReceiveU(String[] args) {
        if (messageQueue.containsKey(args[0])) {
            RoomMessage message = messageQueue.get(args[0]);
            User user = message.getUser();

            if (!user.equals(manager.getLoginUser())) {
                user.setFontColor(message.getFontColor());
                user.setFontFace(message.getFontFace());
                user.setFontSize(message.getFontSize());
                user.setNameColor(message.getNameColor());

                if (banCount != 0
                        && ((System.currentTimeMillis() - banTime) > (30000))) {
                    System.out.println(String.format("Room %s: Banned", name));
                    banCount = 0;
                }
            } else {
                if (banCount != 0) {
                    banCount--;
                    banTime = System.currentTimeMillis();
                }
            }

            messageQueue.remove(args[0]);
            message.attach((Room) this, args[1]);
            addHistory(message);
            fireEvent("message", message.getUser(), message);
        }
    }

    public void onReceiveI(String[] args) {
        long time = Numbers.secondsStringToMilliseconds(args[0]);
        String name = args[1];
        String uniqueId = args[3];
        String sharedId = args[4];
        String id = args[5];
        String IP = args[6].equals("") ? null : args[6];

        String rawMessage = Strings.implode(":", Arrays.copyOfRange(args, 9, args.length));
        Map<String, String> messageData = Misc.getMessageData(rawMessage);
        String body = messageData.get("message");
        String nameData = messageData.get("nameData");
        String fontStyle = messageData.get("fontStyle");
        String nameColor = null;
        String fontColor = null;
        String fontFace = null;
        short fontSize = 0;

        User user;
        RoomMessage message = createMessage(id);

        if (name.equals("")) {
            name = String.format("#%s", args[2]);
            if (name.equals("#")) {
                name = String.format("!anon%s", engine.chatango.util.Misc.getAnonId(nameData, uniqueId));
            }
        } else {
            if (nameData != null && nameData.length() > 0) {
                nameColor = engine.chatango.util.Misc.parseNameColor(nameData);
            }
        }

        if (fontStyle != null && fontStyle.length() > 0) {
            Map<String, Object> fontData = engine.chatango.util.Misc.parseFontStyle(fontStyle);
            fontColor = (String) fontData.get("color");
            fontFace = (String) fontData.get("face");
            fontSize = (short) fontData.get("size");
        }

        user = manager.createUser(name);

        message.setTime(time);
        message.setUser(user);
        message.setBody(body);
        message.setRaw(rawMessage);
        message.setUniqueId(uniqueId);
        message.setIP(IP);
        message.setNameColor(nameColor);
        message.setFontColor(fontColor);
        message.setFontFace(fontFace);
        message.setFontSize(fontSize);
        message.setSharedId(sharedId);
        message.setRoom((Room) this);

        if (!user.equals(manager.getLoginUser())) {
            user.setFontColor(message.getFontColor());
            user.setFontFace(message.getFontFace());
            user.setFontSize(message.getFontSize());
            user.setNameColor(message.getNameColor());
        }

        iLog.add(message);
    }

    public void onReceiveGParticipants(String[] args) {
        String[] participants = Strings.implode(":", args).split("[;]");

        userCount = participants.length;

        for (String participant : participants) {
            String[] dataArray = participant.split("[:]");
            String name = dataArray[3];
            String userId = name.toLowerCase();

            if (!userId.equals("none")) {
                recentList.add(userId);
                User user = manager.createUser(name);
                user.addSessionId((Room) this, dataArray[0]);
                userList.add(user);
            }
        }
    }

    public void onReceiveParticipant(String[] args) {
        String name = args[3];
        String userId = name.toLowerCase();

        if (!userId.equals("none")) {
            User user = manager.createUser(name);

            if (args[0].equals("0")) {
                // leave
                if (recentList.contains(userId)) {
                    recentList.remove(userId);
                }
                user.removeSessionId((Room) this, args[1]);
                userList.remove(user);

                if (!userList.contains(user) || !USER_LIST_EVENT_UNIQUE) {
                    fireEvent("leave", user);
                }
            } else {
                // join
                if (!recentList.contains(userId)) {
                    recentList.add(userId);
                }
                user.addSessionId((Room) this, args[1]);

                boolean doEvent = !userList.contains(user);

                userList.add(user);

                if (doEvent || !USER_LIST_EVENT_UNIQUE) {
                    fireEvent("join", user);
                }
            }
        }
    }

    public void onReceiveShowFw() {
        banCount = 0;
        fireEvent("floodWarning");
    }

    public void onReceiveShowTb() {
        banCount = 0;
        fireEvent("floodBan");
    }

    public void onReceiveTb() {
        banCount = 0;
        fireEvent("floodBanRepeat");
    }

    public void onReceiveDelete(String[] args) {
        RoomMessage message = getMessage(args[0]);

        if (null != message) {
            if (history.contains(message)) {
                history.remove(message);
                fireEvent("message_delete", message.getUser(), message);
                message.detach();
            }
        }
    }

    public void onReceiveDeleteall(String[] args) {
        for (String messageId : args) {
            onReceiveDelete(new String[] {messageId});
        }
    }

    public void onReceiveN(String[] args) {
        userCount = Integer.parseInt(args[0], 16);
        fireEvent("userCountChange");
    }

    public void onReceiveBlocklist(String[] args) {
        String[] sections = Strings.implode(":", args).split("[;]");

        banList.clear();

        for (String section : sections) {
            String[] params = section.split("[:]");

            if (params.length == 5 && !params[2].equals("")) {
                Map<String,  Object> item = new HashMap<>();

                item.put("sharedId", params[0]);
                item.put("IP", params[1]);
                item.put("target", manager.createUser(params[2]));
                item.put("time", Numbers.secondsStringToMilliseconds(params[3]));
                item.put("source", manager.createUser(params[4]));

                banList.add(item);
            }
        }

        fireEvent("banListUpdate");
    }

    public void onReceiveUnblocklist(String[] args) {
        String[] sections = Strings.implode(":", args).split("[;]");

        unbanList.clear();

        for (String section : sections) {
            String[] params = section.split("[:]");

            if (params.length == 5 && !params[2].equals("")) {
                Map<String,  Object> item = new HashMap<>();

                item.put("sharedId", params[0]);
                item.put("IP", params[1]);
                item.put("target", manager.createUser(params[2]));
                item.put("time", Numbers.secondsStringToMilliseconds(params[3]));
                item.put("source", manager.createUser(params[4]));

                unbanList.add(item);
            }
        }

        fireEvent("unbanListUpdate");
    }

    public void onReceiveBlocked(String[] args) {
        if (!args[2].equals("")) {
            Map<String,  Object> item = new HashMap<>();
            User target = manager.createUser(args[2]);
            User source = manager.createUser(args[3]);

            item.put("sharedId", args[0]);
            item.put("IP", args[1]);
            item.put("target", target);
            item.put("source", source);
            item.put("time", Numbers.secondsStringToMilliseconds(args[4]));

            banList.add(item);

            fireEvent("ban", source, target);
            requestBanList();
        }
    }

    public void onReceiveUnblocked(String[] args) {
        if (!args[2].equals("")) {
            Map<String,  Object> item = new HashMap<>();
            User target = manager.createUser(args[2]);
            User source = manager.createUser(args[3]);

            item.put("sharedId", args[0]);
            item.put("IP", args[1]);
            item.put("target", target);
            item.put("source", source);
            item.put("time", Numbers.secondsStringToMilliseconds(args[4]));

            unbanList.add(item);

            fireEvent("unban", source, target);
            requestUnbanList();
        }
    }

    public void onReceiveClearall() {
        fireEvent("clearAll");
    }

    public void onReceiveMiu(String[] args) {

    }

    public void onReceiveUpdateprofile(String[] args) {

    }

    public void onReceiveAnnc(String[] args) {

    }
}