package manager.Command;

import engine.chatango.stream.PM.PM;
import engine.chatango.stream.Room.Room;
import manager.Bot.Bot;
import manager.Help;
import manager.Language;
import org.apache.commons.lang3.text.WordUtils;

import java.lang.reflect.Method;
import java.util.*;

public class CommandBase {
    final private static boolean DEFAULT_LAUNCH_TRESPASS = true;
    final private static boolean DEFAULT_SEND_SIMPLE_MESSAGE_IS_HTML = true;

    protected Bot bot;
    protected Object db;

    protected Room room;
    protected PM pm;

    protected Object realUser;
    protected Object user;
    protected Object realUserData;
    protected Object userData;

    protected byte logLevel = 1;

    protected String language;
    protected String streamName;

    protected long time;
    protected String name;
    protected String prefix;
    protected Map<String, String> details;
    protected List<String> args;
    protected String argsRaw;
    protected Map<String, Object> help;

    /**
     * Launches a command method and outputs the execution time if the log level is at least 1
     * Called in thread
     *
     * @param command name of the command which is going to be called
     * @param trespass true if user access shouldn't be checked, false otherwise
     */
    public void launch(String command, boolean trespass) {
        if (trespass || isAllowed()) {
            if (logLevel > 0 && !trespass) {
                // print('Command execution allowed')
            }
            try {
                getCommandMethodByCommandName(command).invoke(this, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (logLevel > 0) {
            // print('Command execution denied')
        }
    }

    public void launch(String commandName) {
        launch(commandName, DEFAULT_LAUNCH_TRESPASS);
    }

    /**
     * Gets the package name by category name
     * E.g.: the category name is "LEVEL_RELATED", this method will return "LevelRelated"
     *
     * @param category category to return the package name for
     * @return package name
     */
    private String getPackageNameByCategoryName(String category) {
        String[] categoryParts = util.Strings.explode(category, "_");

        for (int i = 0; i < categoryParts.length; i++) {
            categoryParts[i] = WordUtils.capitalizeFully(categoryParts[i]);
        }

        return util.Strings.implode(categoryParts);
    }

    /**
     * Gets the method corresponding for a command
     *
     * @param command the command to get the method for
     * @return a callable method
     */
    private Method getCommandMethodByCommandName(String command) {
        List<String> packageNames = new ArrayList<>();
        Set<String> parents = ((HashMap) Help.getCommandData(command).get("parents")).keySet();

        packageNames.add("commands");
        for (String parent : parents) {
            packageNames.add(getPackageNameByCategoryName(parent));
        }
        packageNames.add("Commands");

        try {
            Class<?> commands = Class.forName(util.Strings.implode(".", packageNames));
            return commands.getMethod(String.format("$%s", command), getClass());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Sends a message to the stream
     *
     * @param message the message to send
     * @param isHTML true if the message should be interpreted as HTML
     */
    public void sendSimpleMessage(String message, boolean isHTML) {
        System.out.println(message);
    }

    public void sendSimpleMessage(String message) {
        sendSimpleMessage(message, DEFAULT_SEND_SIMPLE_MESSAGE_IS_HTML);
    }

    /**
     * Sends a message by using a language key and a hash map
     *
     * @param key key for language string
     * @param language language short name ("en", "ro")
     * @param valuesMap map of values to be applied on the template string
     * @param isHTML true if the message should be interpreted as HTML
     */
    public void sendMessage(String key, Map<String, Object> valuesMap, String language, boolean isHTML) {
        sendSimpleMessage(Language.get(key, language, valuesMap), isHTML);
    }

    public void sendMessage(String key, Map<String, Object> valuesMap, String language) {
        sendSimpleMessage(Language.get(key, language, valuesMap));
    }

    public void sendMessage(String key, Map<String, Object> valuesMap) {
        sendMessage(key, valuesMap, language);
    }

    public void sendMessage(String key, boolean isHTML) {
        sendSimpleMessage(Language.get(key, language), isHTML);
    }

    public void sendMessage(String key) {
        sendSimpleMessage(Language.get(key, language));
    }

    /**
     *
     * @return
     */
    private boolean isAllowed() {
        return true;
    }

    public String userColor() {
        return null;
    }
}