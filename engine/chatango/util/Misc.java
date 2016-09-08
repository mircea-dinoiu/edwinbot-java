package engine.chatango.util;


import org.apache.http.HeaderIterator;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.math.BigInteger;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Misc {
    /**
     * Generate an unique identifier
     * @return a unique value converted to string
     */
    public static String generateUniqueId() {
        BigInteger min = BigInteger.valueOf(10).pow(15);
        BigInteger max = BigInteger.valueOf(10).pow(16).subtract(BigInteger.valueOf(1));

        return String.valueOf(util.Numbers.randomRange(min, max));
    }

    /**
     * Parses the contents of a f tag and returns color, face and size.
     *
     * @param fontStyle font style to parse
     * @return [color=color, face=face, size=size]
     */
    public static Map<String, Object> parseFontStyle(String fontStyle) {
        // ' xSZCOL="FONT"'
        Map<String, Object> fontData = new HashMap<>();
        String color;
        String face;
        short size;

        try {
            String[] tempData = fontStyle.split("[=]", 2);
            String sizeAndColor = tempData[0].trim();

            size = Short.parseShort(sizeAndColor.substring(1, 3));
            color = sizeAndColor.substring(3, 6);

            if (color.equals("")) {
                color = null;
            }

            face = fontStyle.split("[\"]", 3)[1];
        } catch (Exception e) {
            color = null;
            face = null;
            size = 0;
        }

        fontData.put("color", color);
        fontData.put("face", face);
        fontData.put("size", size);

        return fontData;
    }

    /**
     * This just returns its argument, should return the name color.
     *
     * @param nameColor name color to parse
     * @return parsed name color
     */
    public static String parseNameColor(String nameColor) {
        return nameColor;
    }

    /**
     * Get the id of an anonymous user.
     *
     * @param nameData name data
     * @param uniqueId unique id
     * @return anonymous user's id
     */
    public static String getAnonId(String nameData, String uniqueId) {
        if (nameData == null) {
            nameData = "5504";
        }

        try {
            int length = Math.min(nameData.length(), uniqueId.length() - 4);
            StringBuilder result = new StringBuilder(length);
            short[] l1 = new short[length];
            short[] l2 = new short[length];
            int l1index = 0,
                l2index = 0;

            for (int index = 0; index < nameData.length() && l1index < length; index++) {
                l1[l1index] = Short.parseShort(nameData.substring(index, index+1));
                l1index++;
            }

            for (int index = 4; index < uniqueId.length() && l2index < length; index++) {
                l2[l2index] = Short.parseShort(uniqueId.substring(index, index + 1));
                l2index++;
            }

            for (int index = 0; index < length; index++) {
                result.append((l1[index] + l2[index]) % 10);
            }

            return result.toString();
        } catch (Exception e) {
            return "NNNN";
        }
    }

    /**
     * Request an authentication ID using name and password.
     *
     * @param name username
     * @param password password
     * @return auth ID if the login data is correct, null otherwise
     */
    public static String getAuthId(String name, String password) {
        URI uri;
        HttpPost request;
        CloseableHttpClient httpClient = HttpClients.createMinimal();
        CloseableHttpResponse response;
        Pattern p = Pattern.compile("auth\\.chatango\\.com ?= ?([^;]*)");
        Matcher m;
        String authId = null;

        try {
            uri = new URIBuilder()
                    .setScheme("http")
                    .setHost("chatango.com")
                    .setPath("/login")
                    .setParameter("user_id", name)
                    .setParameter("password", password)
                    .setParameter("storecookie", "on")
                    .setParameter("checkerrors", "yes")
                    .build();

            request = new HttpPost(uri);
            response = httpClient.execute(request);

            HeaderIterator it = response.headerIterator("Set-Cookie");

            while (it.hasNext()) {
                m = p.matcher(it.next().toString());

                if (m.find()) {
                    if (m.group(1).length() > 0) {
                        authId = m.group(1);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return authId;
    }
}
