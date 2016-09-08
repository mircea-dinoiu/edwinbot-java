package manager.Bot;

import engine.chatango.common.RoomMessage;
import engine.chatango.common.User;
import engine.chatango.stream.Room.Room;
import lib.YoutubeVideo;
import manager.Help;
import manager.Language;
import util.Localization;
import util.Render;
import util.Strings;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BotUtils extends BotBase {
    final private static boolean DEFAULT_GET_COMMAND_RETURN_DETAILS = false;
    final private static String DEFAULT_EXTRACT_COMMAND_EXTRA_PREFIXES = "";

    private Queue<Map<String, Object>> messagesToInspect;

    public BotUtils(Object... args) throws Exception {
        super(args);
    }

    /**
     * Start threads
     */
    protected void startThreads() {
        new Thread() {
            @Override
            public void run() {
                cronTick();
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                inspectMessages();
            }
        }.start();
    }

    /**
     * Add a message to the queue to be inspected.
     *
     * @param message message to be inspected
     * @param user message's owner
     * @param room room where the message came from
     */
    protected void addMessageToInspect(RoomMessage message, User user, Room room) {
        Map<String, Object> messageToInspect = new HashMap<>();

        messageToInspect.put("message", message);
        messageToInspect.put("user", user);
        messageToInspect.put("room", room);

        messagesToInspect.add(messageToInspect);
    }

    /**
     * Inspect messages method
     *
     * Running all the time in a separate thread, looks over the messages to inspect
     * queue and if there is a message there, calls the inspection methods and pops
     * out the message.
     */
    protected void inspectMessages() {
        messagesToInspect = new LinkedList<>();

        while (running) {
            if (messagesToInspect.size() > 0) {
                Map<String, Object> messageToInspect = messagesToInspect.poll();
                RoomMessage message = (RoomMessage) messageToInspect.get("message");
                User user = (User) messageToInspect.get("user");
                Room room = (Room) messageToInspect.get("room");

                seen(message, user, room);
                listen(message, user, room);
                collectWhoIsData(message, user);
            }
        }
    }

    private void seen(RoomMessage message, User user, Room room) {
        // TODO
    }

    private void listen(RoomMessage message, User user, Room room) {
        // TODO
    }

    private void collectWhoIsData(RoomMessage message, User user) {
        // TODO
    }

    /**
     * Set user style for the bot
     *
     * Activates:
     * - name color
     * - font color
     * - font face
     * - font size
     * - background
     * - recording
     */
    protected void setStyle() {
        // TODO REPLACE HARDCODINGS
        String nameColor = "000000";
        String fontColor = "333333";
        String fontFace = "Tahoma";
        short fontSize = 12;

        setNameColor(nameColor);
        setFontColor(fontColor);
        setFontFace(fontFace);
        setFontSize(fontSize);

        enableBackground();
        enableRecording();
    }

    /**
     * HTTPS or Youtube feature
     * Checks if the message is a HTTPS or Youtube link
     *
     * @param room room where the message came from
     * @param messageBody message's body
     * @return true if the message is a HTTPS or Youtube link, false otherwise
     */
    protected boolean HTTPSOrYoutube(Room room, String messageBody) {
        Pattern URLObjectPattern = Pattern.compile(
            "^(https|http)://([^ ]+)$",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        );
        Matcher URLObjectMatcher = URLObjectPattern.matcher(messageBody);

        if (URLObjectMatcher.find()) {
            String URL;
            boolean failure = false;
            boolean isHTTPS;

            URL = String.format("http://%s", URLObjectMatcher.group(2));
            isHTTPS = (URLObjectMatcher.group(1).equalsIgnoreCase("https"));

            try {
                String videoId = null;
                Pattern regexPattern;
                Matcher regexMatcher;
                YoutubeVideo video;

                if (URL.toLowerCase().contains("youtube.com")) {
                    regexPattern = Pattern.compile(
                        "(\\?|&)v=([^&\\?/ ]+)",
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
                    );
                    regexMatcher = regexPattern.matcher(URL);

                    while (regexMatcher.find()) {
                        videoId = regexMatcher.group(2);
                    }
                } else if (URL.toLowerCase().contains("youtu.be")) {
                    regexPattern = Pattern.compile(
                        "youtu\\.be/([^&?/ ]+)",
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
                    );
                    regexMatcher = regexPattern.matcher(URL);

                    while (regexMatcher.find()) {
                        videoId = regexMatcher.group(2);
                    }
                }

                video = new YoutubeVideo(videoId);

                Map<String, Object> valuesMap = new HashMap<>();
                String message;

                valuesMap.put("title", Render.highlight(video.getTitle(), new String[] {"b"}));
                valuesMap.put("author", Render.highlight(video.getAuthor(), new String[] {"u"}));
                valuesMap.put("url", isHTTPS ? video.getURL() : "");

                message = Language.get("YOUTUBE", "ro", valuesMap);

                room.sendMessage(message);
            } catch (Exception e) {
                failure = true;
            }

            if (failure && isHTTPS) {
                room.sendMessage(URL);
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Get a room color
     *
     * The color is:
     *  `Red` if the bot is present just as an anonymous user on the room
     *  `Yellow` if the bot cannot talk on the room
     *  `Blue` if the room is considered home
     *  `Green` if the bot is connected to the room
     *  `Gray` if the bot is not connected to the room
     *
     * @param roomName the room name
     * @return colored room name
     */
    public String roomColor(String roomName) {
        Room room;
        String color;
        String[] styles = {"u"};

        roomName = roomName.toLowerCase();
        room = getRoom(roomName);

        if (null != room) {
            if (room.getLoginFail()) {
                color = "Red";
            } else if (room.isSleeping() || room.isLocked()) {
                color = "Yellow";
            } else if (room.isHome()) {
                color = "Blue";
            } else {
                color = "Green";
            }
        } else {
            color = "Gray";
        }

        return Render.highlight(roomName, color, styles);
    }

    /**
     *
     * @param user
     * @param realUser
     * @param room
     * @param useNick
     * @param PM
     * @return
     */
    public String userColor(User user, User realUser, Room room, boolean useNick, boolean PM) {
        // TODO
        return "";
    }

    /**
     * Get a command by its name, its alias or by a substring.
     *
     * @param query the command name to search for
     * @param returnDetails true to return more info about the query, false to return the command name
     * @return the original command name if it's documented,
     *         otherwise if returnDetails is true  : [name=null]
     *                                       false : null
     */
    public Object getCommand(String query, boolean returnDetails) {
        String commandName;
        Map<String, String> details = new HashMap<>();

        query = query.trim().toLowerCase();

        if (0 == query.length()) {
            commandName = null;
        } else {
            commandName = Help.getCommandName(query);

            if (null == commandName) {
                for (String each : Help.getOrderedCommandNamesAndAliases()) {
                    if (each.startsWith(query)) {
                        if (returnDetails) {
                            details.put("match", each);
                            details.put("queryType", "shortcut");
                        }

                        commandName = Help.getCommandName(each);
                        break;
                    }
                }
            } else if (returnDetails) {
                details.put("match", query);

                if (commandName.equals(query)) {
                    details.put("queryType", "command");
                } else {
                    details.put("queryType", "alias");
                }
            }
        }

        if (returnDetails) {
            details.put("name", commandName);
            return details;
        } else {
            return commandName;
        }
    }

    public Object getCommand(String query) {
        return getCommand(query, DEFAULT_GET_COMMAND_RETURN_DETAILS);
    }

    /**
     * Get a command by its name, its alias or by a substring
     *
     * @param messageBody the message to extract the command from
     * @param extraPrefixes string containing extra prefixes specific to some commands
     * @return the command data
     */
    protected Map<String, Object> extractCommand(String messageBody, String extraPrefixes) {
        Map<String, Object> data = new HashMap<>();
        Set<Character> commandPrefixes = new HashSet<>();
        commandPrefixes.add('/');
        commandPrefixes.add(';');
        // TODO CHANGE THIS WITH DB GETTING OF COMMAND PREFIXES

        data.put("name", null);

        if (messageBody.length() > 1) {
            char prefix = messageBody.charAt(0);
            if (-1 != extraPrefixes.indexOf(prefix)
                    || commandPrefixes.contains(prefix)) {
                String[] array, args = {};
                String command, argsRaw = "";
                Map<String, String> commandData;

                array = Strings.explode(messageBody, 2);
                command = array[0].toLowerCase().substring(1);
                commandData = (Map) getCommand(command, true);

                if (null != commandData.get("name")) {
                    if (2 == array.length) {
                        argsRaw = array[1];
                        args = Strings.explode(argsRaw);
                    }

                    data.putAll(commandData);
                    data.put("prefix", prefix);
                    data.put("argsRaw", argsRaw);
                    data.put("args", args);
                }
            }
        }

        return data;
    }

    protected Map<String, Object> extractCommand(String messageBody) {
        return extractCommand(messageBody, DEFAULT_EXTRACT_COMMAND_EXTRA_PREFIXES);
    }

    /**
     * Format coins using format number util method
     *
     * @param coins coins
     * @param language language to use
     * @return formatted coins
     */
    public String formatCoins(Number coins, String language) {
        String niceNumber = Render.currency(coins.longValue());
        niceNumber = Render.highlight(niceNumber, "WashedPrune");

        return Localization.formatNumber(
            coins,
            Language.get("COINS", language),
            Language.get("COIN", language),
            language,
            niceNumber
        );
    }
}
