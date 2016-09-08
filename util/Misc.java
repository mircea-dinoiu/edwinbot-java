package util;

import common.exceptions.PremiumTimeException;
import engine.chatango.common.User;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Misc {
    /**
     * Get level price
     *
     * @param level the level to get the price for
     * @return level's price
     */
    public static long getLevelPrice(int level) {
        return Math.round(Math.pow(Math.sqrt(level), Math.sqrt(level))) * 60;
    }

    /**
     * Chatango specific feature

     * An anonymous user can have:
     * "!" before name meaning that the anonymous user has no name
     * "#" before name meaning that the anonymous user has a name
     * "*" before name meaning that the anonymous user sent a PM message to the bot
     *
     * @param userId user's id
     */
    public static boolean isAnon(String userId) {
        String[] anonPrefixes = {"!", "#", "*"};

        for (String anonPrefix : anonPrefixes) {
            if (userId.startsWith(anonPrefix)) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAnon(User user) {
        return isAnon(user.getId());
    }

    /**
     * Get URL content
     *
     * @param url url to get the content from
     * @return content
     * @throws IOException
     */
    public static String getURLContent(String url) throws IOException {
        InputStream in;
        String content;

        in = new URL(url).openStream();
        content = IOUtils.toString(in);
        IOUtils.closeQuietly(in);

        return content;
    }

    /**
     * Get premium time of a user
     *
     * @param userId user's id
     * @return user's last bg expiration time
     * @throws PremiumTimeException if user never had premium
     */
    public static long getPremiumExpirationTime(String userId) throws PremiumTimeException {
        String url,
               raw;
        Pattern p = Pattern.compile("<d>([0-9]*)</d>");
        Matcher m;

        url = String.format(
            "http://pst.chatango.com/profileimg/%s/%s/%s/mod1.xml",
            userId.charAt(0),
            userId.length() > 1 ? userId.charAt(1) : userId.charAt(0),
            userId
        );

        try {
            raw = getURLContent(url);
            m = p.matcher(raw);

            if (m.find()) {
                return Long.valueOf(m.group(1));
            } else {
                throw new PremiumTimeException();
            }
        } catch (Exception e) {
            throw new PremiumTimeException();
        }
    }

    /**
     * Clean the message and return the name color, font style and the cleaned message.
     *
     * @param rawMessage the message
     * @return [nameData=nameData, fontStyle=fontStyle, message=message]
     */
    public static Map<String, String> getMessageData(String rawMessage) {
        Map<String, String> messageData = new HashMap<>();
        String nameData = null,
               fontStyle = null,
               message;
        Pattern p;
        Matcher m;

        p = Pattern.compile("<n(.*?)/>");
        m = p.matcher(rawMessage);
        if (m.find()) {
            nameData = m.group(1);
        }

        p = Pattern.compile("<f(.*?)>");
        m = p.matcher(rawMessage);
        if (m.find()) {
            fontStyle = m.group(1);
        }

        message = rawMessage
                .replaceAll("<n.*/>", "")
                .replaceAll("<f.*/>", "")
                .replace("</p><p>", "\n")
                .replace("\n", "<br>")
                .replace("\n", "<br>")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&amp;", "&");

        messageData.put("nameData", nameData);
        messageData.put("fontStyle", fontStyle);
        messageData.put("message", message);

        return messageData;
    }
}
