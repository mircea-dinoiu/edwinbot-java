package engine.chatango.stream.PM;

import engine.chatango.common.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PMUtils extends PMBase {
    final private static boolean DEFAULT_SEND_MESSAGE_IS_HTML = true;

    /**
     * Sends a message to a user
     *
     * @param user user to send the message to
     * @param message message to be sent
     * @param isHTML false to encode html tags
     */
    public void sendMessage(User user, String message, boolean isHTML) {
        setActive();
        idle = false;
        User loginUser = manager.getLoginUser();
        StringBuilder messageBuilder = new StringBuilder();

        if (!isHTML) {
            messageBuilder.append(String.format(
                "<n%s/>" +
                        "<m v=\"1\">" +
                        "<g xs0=\"0\">" +
                        "<g x%ss%s=\"%s\">%s</g>" +
                        "</g>" +
                        "</m>",
                loginUser.getNameColor(),
                loginUser.getFontSizeForMessage(),
                loginUser.getFontColor().toLowerCase(),
                loginUser.getFontFace(),
                message.replace("<", "&lt;").replace(">", "&gt;").trim()
            ));
        } else {
            // Replace tags
            message = message.replace("<b>", "<B>")
                    .replace("<u>", "<U>")
                    .replace("<i>", "<I>")
                    .replace("</b>", "</B>")
                    .replace("</u>", "</U>")
                    .replace("</i>", "</I>");

            Pattern p = Pattern.compile("<font color=\"#([0-9A-F]+)\">(.*?)</font>");
            Matcher m = p.matcher(message);

            while (m.find()) {
                message = m.replaceFirst(String.format(
                        "<g x%ss%s=\"%s\">%s</g>",
                        loginUser.getFontSizeForMessage(),
                        loginUser.getFontFace(),
                        m.group(1).toLowerCase(),
                        m.group(2)
                ));
            }

            String[] lines = message.split("<br>"); // TODO REPLACE WITH LINE BREAKER GLOBAL CONFIG

            messageBuilder.append(String.format("<n%s/><m v=\"1\">", loginUser.getNameColor()));

            if (lines.length == 1) {
                messageBuilder.append(String.format(
                        "<g xs0=\"0\">" +
                            "<g x%ss%s=\"%s\">%s</g>" +
                        "</g>",
                        loginUser.getFontSizeForMessage(),
                        loginUser.getFontColor().toLowerCase(),
                        loginUser.getFontFace(),
                        lines[0].trim()
                ));
            } else {
                for (String line : lines) {
                    messageBuilder.append(String.format(
                            "<P>" +
                                "<g xs0=\"0\">" +
                                    "<g x%ss%s=\"%s\">%s</g>" +
                                "</g>" +
                            "</P>",
                            loginUser.getFontSizeForMessage(),
                            loginUser.getFontColor().toLowerCase(),
                            loginUser.getFontFace(),
                            line.trim()
                    ));
                }
            }

            messageBuilder.append("</m>");
        }

        sendDelayedCommand("msg", user.getId(), messageBuilder.toString());
    }

    public void sendMessage(User user, String message) {
        sendMessage(user, message, DEFAULT_SEND_MESSAGE_IS_HTML);
    }

    public void addContact(User user) {
        if (!contacts.contains(user)) {
            sendCommand("wladd", user.getId());
            contacts.add(user);
            fireEvent("PMContactAdd", user);
        }
    }

    public void removeContact(User user) {
        if (contacts.contains(user)) {
            sendCommand("wldelete", user.getId());
            contacts.remove(user);
            fireEvent("PMContactRemove", user);
        }
    }

    public void block(User user) {
        if (!blockList.contains(user)) {
            sendCommand("block", user.getId(), user.getId(), "S");
            blockList.add(user);
            fireEvent("PMBlock", user);
        }
    }

    public void unblock(User user) {
        if (blockList.contains(user)) {
            sendCommand("unblock", user.getId());
            blockList.remove(user);
            fireEvent("PMUnblock", user);
        }
    }
}
