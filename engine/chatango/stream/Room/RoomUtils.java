package engine.chatango.stream.Room;

import engine.chatango.common.RoomMessage;
import engine.chatango.common.User;
import org.apache.commons.lang3.StringUtils;
import util.HTML;
import util.Numbers;
import util.Strings;

import java.io.IOException;
import java.util.List;
import java.util.Map;

class RoomUtils extends RoomBase {
    final private static boolean DEFAULT_SEND_MESSAGE_STYLED = true;
    final private static boolean DEFAULT_SEND_MESSAGE_SPLIT = false;
    final private static boolean DEFAULT_SEND_MESSAGE_IS_HTML = true;

    /**
     * Send a message.
     *
     * @param message message
     * @param isHTML true if the HTML tags should be converted to HTML entities, false otherwise
     * @param styled true to send the message with the f and n tags, false otherwise
     * @param split true if the message should be split in more messages, false otherwise
     */
    public void sendMessage(String message, boolean isHTML, boolean styled, boolean split) {
        User loginUser = manager.getLoginUser();

        if (!bannedWords.isEmpty()) {
            for (String bannedWord : bannedWords) {
                message = message.replace(bannedWord, HTML.HTMLEntities(bannedWord));
            }
        }

        if (!isHTML) {
            message = message.replace("<", "&lt;").replace(">", "&gt;");
        }

        if (split && message.length() > MAX_LENGTH) {
            if (BIG_MESSAGE_CUT) {
                sendMessage(message.substring(0, MAX_LENGTH), isHTML, styled, true);
            } else if (BIG_MESSAGE_MULTIPLE) {
                while (message.length() > 0) {
                    String section = message.substring(0, MAX_LENGTH);
                    message = message.substring(MAX_LENGTH, message.length());
                    sendMessage(section, isHTML, styled, false);
                }
            }
        } else {
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append(message);

            if (sleeping) {
                messageBuilder.insert(0, "zzzz ");
            }

            if (styled) {
                messageBuilder.insert(0, String.format(
                    "<n%s/>",
                    loginUser.getNameColor()
                ));
                messageBuilder.insert(0, String.format(
                    "<f x%s%s=\"%s\">",
                    loginUser.getFontSizeForMessage(),
                    loginUser.getFontColor(),
                    loginUser.getFontFace()
                ));
            }

            if (null != mode) {
                messageBuilder.append(mode);
            }

            messageBuilder.append(StringUtils.repeat(" ", Numbers.randomRange(0, 9)));

            sendDelayedCommand("bmsg:tl2r", messageBuilder.toString());
        }

        if (banCount == 0) {
            banTime = System.currentTimeMillis();
        }
        banCount++;
    }

    public void sendMessage(String message, boolean isHTML, boolean styled) {
        sendMessage(message, isHTML, styled, DEFAULT_SEND_MESSAGE_SPLIT);
    }

    public void sendMessage(String message, boolean isHTML) {
        sendMessage(message, isHTML, DEFAULT_SEND_MESSAGE_STYLED);
    }

    public void sendMessage(String message) {
        sendMessage(message, DEFAULT_SEND_MESSAGE_IS_HTML);
    }

    /**
     * Set your background. The color must be an html color code.
     * The image parameter takes a boolean to turn the picture off or on.
     * Transparency is a float less than one or an integer between 1-100.
     *
     * @param color color to set
     * @return true if background was successfully set, false if there is no
     * premium feature for the login user or the background couldn't be set
     */
    public boolean setBackground(String color) {
        /*
            # Get the original settings
            #letter1 = self.mgr.user.uid[0]
            #letter2 = self.mgr.user.uid[1] if len(self.mgr.user.uid) > 1 else self.mgr.user.uid[0]
            #data = urllib.request.urlopen(
            #    "http://fp.chatango.com/profileimg/%s/%s/%s/msgbg.xml" % (
            #        letter1,
            #        letter2,
            #        self.user.uid
            #    )
            #).read().decode()
            #data = dict([
            #    x.replace('"', '').split("=")
            #    for x in re.findall('(\w+=".*?")', data)[1:]
            #])
            # Add the necessary shiz
            #data["p"] = self.mgr.password
            #data["lo"] = self.mgr.user.uid
            #if color3x:
            #    data["bgc"] = color3x
            # Send the request
            #data = urllib.parse.urlencode(data)
         */
        if (premium) {
            try {
                util.Misc.getURLContent(String.format(
                        "http://chatango.com/updatemsgbg?bgc=%s&hasrec=0" +
                        "&p=%s&isvid=0&lo=%s&align=br&bgalp=100&useimg=1" +
                        "&ialp=50&tile=1",
                        color,
                        manager.getPassword(),
                        manager.getLoginUser().getId()
                ));
                return true;
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public void addBannedWord(String word) {
        if (getLevel(manager.getLoginUser().getId()) == 2) {
            bannedWords.add(word);
            sendCommand("setbannedwords", "403", Strings.implode(", ", bannedWords));
        }
    }

    public void removeBannedWord(String word) {
        if (getLevel(manager.getLoginUser().getId()) == 2) {
            bannedWords.remove(word);
            sendCommand("setbannedwords", "403", Strings.implode(", ", bannedWords));
        }
    }

    /**
     * Add a moderator.
     *
     * @param user user to add as moderator
     */
    public void addModerator(User user) {
        if (getLevel(manager.getLoginUser().getId()) == 2) {
            sendCommand("addmod", user.getId());
        }
    }

    /**
     * Remove a moderator.
     *
     * @param user user to remove from moderator list
     */
    public void removeModerator(User user) {
        if (getLevel(manager.getLoginUser().getId()) == 2) {
            sendCommand("removemod", user.getId());
        }
    }

    /**
     * Flag a message.
     *
     * @param message message to flag
     */
    public void flag(RoomMessage message) {
        sendCommand("g_flag", message.getId());
    }

    /**
     * Flag a user.
     *
     * @param user user to flag
     * @return true if a message to flag was found, false otherwise
     */
    public boolean flagUser(User user) {
        RoomMessage message = getLastMessage(user);

        if (null != message) {
            flag(message);
            return true;
        }

        return false;
    }

    /**
     * Delete a message.
     * (Moderator only)
     *
     * @param message message to delete
     */
    public void delete(RoomMessage message) {
        if (getLevel(manager.getLoginUser().getId()) > 0) {
            sendCommand("delmsg", message.getId());
        }
    }

    public void rawClearUser(String sharedId) {
        sendCommand("delallmsg", sharedId);
    }

    /**
     * Clear all of user's messages.
     * (Moderator only)
     *
     * @param user user to delete messages of
     * @return true a message to delete was found, false otherwise
     */
    public boolean clearUser(User user) {
        if (getLevel() > 0) {
            RoomMessage message = getLastMessage(user);

            if (null != message) {
                rawClearUser(message.getSharedId());
            }

            return true;
        }

        return false;
    }

    /**
     * Clear all message.
     * (Owner only)
     */
    public void clearAll() {
        if (getLevel(manager.getLoginUser().getId()) == 2) {
            sendCommand("clearall");
        }
    }

    /**
     * Request an updated ban list.
     */
    protected void requestBanList() {
        sendCommand("blocklist", "block", "", "next", "500");
    }

    /**
     * Request and updated unban list.
     */
    protected void requestUnbanList() {
        sendCommand("blocklist", "unblock", "", "next", "500");
    }

    /**
     * Execute the block command using specified arguments.
     * (For advanced usage)
     *
     * @param id user's id
     * @param ip user's ip
     * @param sharedId user's shared id
     */
    public void rawBan(String id, String ip, String sharedId) {
        sendCommand("block", sharedId, ip, id);
    }

    /**
     * Ban a message's sender.
     * (Moderator only)
     *
     * @param message message to ban the sender of
     */
    public void ban(RoomMessage message) {
        if (getLevel(manager.getLoginUser().getId()) > 0) {
            rawBan(message.getUser().getId(), message.getIP(), message.getSharedId());
        }
    }

    /**
     * Ban a user.
     * (Moderator only)
     *
     * @param user user to ban
     * @return true if a message to ban the owner user was found
     */
    public boolean banUser(User user) {
        RoomMessage message = getLastMessage(user);

        if (null != message) {
            ban(message);
            return true;
        }

        return false;
    }

    /**
     * Execute the unblock command using the specified arguments.
     * (For advanced usage)
     *
     * @param id user's id
     * @param ip user's ip
     * @param sharedId user's shared id
     */
    public void rawUnban(String id, String ip, String sharedId) {
        sendCommand("removeblock", sharedId, ip, id);
    }

    /**
     * Unban a user.
     * (Moderator only)
     *
     * @param user user to unban
     * @return true if the user was unbanned, false otherwise
     */
    public boolean unban(User user) {
        Map record = getBanRecord(user);

        if (null != record) {
            rawUnban(((User) record.get("source")).getId(), (String) record.get("IP"), (String) record.get("sharedId"));
            return true;
        }

        return false;
    }

    /**
     * Util methods
     */
    public short getLevel(String id) {
        if (id.equals(owner.getId())) {
            return 2;
        }

        for (User moderator : moderators) {
            if (id.equals(moderator.getId())) {
                return 1;
            }
        }

        return 0;
    }

    public short getLevel(User user) {
        return getLevel(user.getId());
    }

    public short getLevel() {
        return getLevel(manager.getLoginUser());
    }

    public RoomMessage getLastMessage(User user) {
        RoomMessage message;

        if (null != user) {
            int i = 1;
            try {
                while (true) {
                    message = history.get(history.size() - i);
                    if (message.getUser().equals(user)) {
                        break;
                    }
                    i++;
                }
            } catch (Exception e) {
                message = null;
            }
        } else {
            try {
                message = history.get(history.size() - 1);
            } catch (Exception e) {
                message = null;
            }
        }

        return message;
    }

    public RoomMessage getLastMessage() {
        return getLastMessage(null);
    }

    /**
     * Get message by message id
     *
     * @param id message's id
     * @return message
     */
    public RoomMessage getMessage(String id) {
        return messages.containsKey(id) ? messages.get(id) : null;
    }

    /**
     * Add a message to history.
     *
     * @param message message to add
     */
    public void addHistory(RoomMessage message) {
        history.add(message);
        if (history.size() > MAX_HISTORY_LENGTH) {
            List<RoomMessage> rest = history.subList(0, MAX_HISTORY_LENGTH);
            List<RoomMessage> newHistory = history.subList(MAX_HISTORY_LENGTH, history.size());

            history.clear();
            history.addAll(newHistory);

            for (RoomMessage eachMessage : rest) {
                eachMessage.detach();
            }
        }
    }

    public Map<String, Object> getBanRecord(User user) {
        for (Map<String, Object> record : banList) {
            if (record.get("user").equals(user)) {
                return record;
            }
        }

        return null;
    }

    public RoomMessage createMessage(String id) {
        RoomMessage message;

        if (!messages.containsKey(id)) {
            message = new RoomMessage(id);
            messages.put(id, message);
        } else {
            message = messages.get(id);
        }

        return message;
    }

    @Override
    public void recalculateWaitUntil() {
        waitUntil = System.currentTimeMillis() + (rateLimit > 0 ? (rateLimit + 1) : delay);
    }
}
