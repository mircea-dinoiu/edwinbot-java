package manager.Command;

import engine.chatango.common.Stream.Stream;
import engine.chatango.common.User;
import engine.chatango.stream.PM.PM;
import engine.chatango.stream.Room.Room;
import manager.Bot.Bot;
import manager.Help;
import util.Render;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Command extends CommandUtils {
    public Command(
        Map<String, Object> command,
        Stream stream,
        Map<String, Object> realUserData,
        Map<String, Object> userData,
        User realUser,
        User user
    ) {
        // Bot
        bot = (Bot) stream.getManager();

        // Stream related
        if (stream instanceof Room) {
            room = (Room) stream;
            language = room.getLanguage();
        } else {
            pm = (PM) stream;
            language = (String) realUserData.get("language");
        }
        streamName = stream.getName();

        // Command specific
        time = (long) command.get("time");
        name = (String) command.get("name");
        prefix = (String) command.get("prefix");
        details = (Map<String, String>) command.get("details");
        args = (List<String>) command.get("args");
        argsRaw = (String) command.get("argsRaw");
        help = Help.getCommandData(name);

        if (!name.equals("unpark") && (boolean) userData.get("parked")
            && !(
                !user.equals(realUser)
                && name.equals("shapeshift")
            )) {
            Map<String, Object> valuesMap = new HashMap<>();

            valuesMap.put("me", userColor());
            valuesMap.put("command", Render.highlight(String.format("%sunpark", prefix), "Blue"));

            sendMessage("ERROR_ACCOUNT_PARKED", valuesMap);
        } else if (null != pm && !((boolean) help.get("pm"))) {
            sendMessage("ERROR_COMMAND_NOT_AVAILABLE_IN_PM");
        } else if (null != room && !((boolean) help.get("room"))) {
            Map<String, Object> valuesMap = new HashMap<>();

            valuesMap.put("me", userColor());

            sendMessage("ERROR_COMMAND_NOT_AVAILABLE_IN_CHATROOMS", valuesMap);
        } else {
            // todo thread
            launch(name);
        }
    }
}