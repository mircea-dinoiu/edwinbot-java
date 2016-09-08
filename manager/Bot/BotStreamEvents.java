package manager.Bot;

import engine.chatango.common.RoomMessage;
import engine.chatango.common.Stream.Stream;
import engine.chatango.common.User;
import engine.chatango.stream.PM.PM;
import engine.chatango.stream.Room.Room;
import manager.Command.Command;
import org.apache.commons.lang3.ArrayUtils;
import util.Misc;

import java.util.HashMap;
import java.util.Map;

public class BotStreamEvents extends BotUtils {
    final private static String[] MODERATOR_COMMANDS = {
        "startgame", "pausegame", "resumegame",
        "sleep", "wakeup",
        "mode",
        "pdelete", "delete", "clearrecent"
    };

    public BotStreamEvents(Object... args) throws Exception {
        super(args);
    }

    public void onRaw(Stream stream, String raw) {
        // TODO USE LOG LEVEL > 2
//        System.out.println(raw);
    }

    public void onConnect(Room room) {
        /*
            room_data = self.db.get_room_data(room.name)

            # Getting sleep mode from database for each room
            room.game = room_data['game']
            room.sleep = bool(room_data['sleep'])
            room.lang = room_data['lang']
            room.default = bool(room_data['default'])
            room.delay = float(room_data['delay'])
         */
        room.setLanguage("en"); // TODO REMOVE THIS

        setStyle();

        System.out.println(String.format(
                "Room %s: Successfully connected [Sleep mode: %s; Language: %s; Home: %s]",
                room.getName(),
                room.isSleeping() ? "On" : "Off",
                room.getLanguage(), // TODO lang=self.db.store['languages'][room.lang],
                room.isHome() ? "Yes" : "No"
        ));
    }

    public void onUserCountChange(Room room) {}

    public void onReconnect(Room room) {
        System.out.println(String.format("%s: reconnected", room.getName()));
    }

    public void onDisconnect(Room room) {
        System.out.println(String.format("%s: disconnected", room.getName()));
    }

    public void onMessage(Room room, User user, RoomMessage message) {
        String messageBody = message.getBody().trim();

        // Just spy things :)
        addMessageToInspect(message, user, room);

        // Get the real user
        User realUser = user;

        /*
            TODO

            # Shapeshifting
            if self.db.store['shapeshiftings'].get(real_user.uid):
                user = get_user(self.db.store['shapeshiftings'][real_user.uid])
         */

        if (!room.isLocked()) {
            boolean messageSentByBot = user.equals(getLoginUser()) && user.equals(realUser);

            // Ignoring messages sent by bot and empty messages
            if (!messageSentByBot && messageBody.length() > 0) {
                Map<String, Object> command = extractCommand(messageBody);

                try {
                    // Deleting messages of other users
                    // Check if bot is moderator on current chatroom
                    boolean isBotModerator = (room.getLevel() > 0);

                    // Auto-delete/auto-ban triggers
                    if (isBotModerator) {
                        /*
                            TODO

                            if user.uid in self.db.store['auto_delete'] and user.uid == real_user.uid:
                                room.clear_user(user)
                                return
                         */
                    }

                    // Flood warning
                    if (room.getFlood() > 0 && System.currentTimeMillis() > room.getFlood()) {
//                        room_data = self.db.get_room_data(room.name) TODO
                        room.setFlood(0);
//                        room.delay = room_data['delay'] TODO
                    }

                    // Get user data from the database
//                    user_data = self.db.get_user_data(user.uid) TODO
//                    real_user_data = self.db.get_user_data(real_user.uid) TODO
                    Map<String, Object> userData = new HashMap<>();
                    Map<String, Object> realUserData = new HashMap<>();

//                    # Room redirecting TODO
//                    room = self.redirect(room, real_user, real_user_data, user, msg, command) TODO

                    // Ignore everything but wakeup command when sleeping
                    /* TODO
                        if (
                            room.sleep
                            and command['name'] != 'wakeup'
                            and not user_data['admin']
                            and not real_user_data['admin']
                        ):
                            return
                     */

                    // HTTPS links and Youtube feature
                    if (HTTPSOrYoutube(room, messageBody)) {
                        return;
                    }

                    // Allow moderators to execute some commands without being whitelisted
                    boolean isUserModerator = (room.getLevel(user) > 0);
                    boolean hasModeratorCommands = isUserModerator && ArrayUtils.contains(MODERATOR_COMMANDS, command.get("name"));

                    // Ignore anonymous users, ignored and unknown users
                    boolean anonOrIgnored = Misc.isAnon(user) || (boolean) userData.get("ignored");
                    boolean unknown = (short) userData.get("level") == 0 && !command.get("name").equals("help");

                    if ((unknown || anonOrIgnored)
                            && !hasModeratorCommands
                            && !command.get("name").equals("shapeshift")) {
                        if (!anonOrIgnored && null != command.get("name")) {
                            /*
                                TODO

                                room.message(self.lang('ERROR_NOT_WHITELISTED', room.lang).format(
                                    me=self.user_color(user=user, real_user=real_user, room=room, use_nick=False, pm=False),
                                    command=highlight(command['prefix'] + 'help', 'Blue')
                                ))
                             */
                        }
                        return;
                    }

                    /**
                     * If we came across until here, we will want to understand the command
                     */

                    // Stop any game if an error occurred or there are too few users
                    // self.game_check_room_user_count(room) TODO

                    if (null == command.get("name")) {
                        if (messageBody.toLowerCase().contains(getLoginUserId())) {
                            // If a user wants to speak with the bot
                            messageBody = String.format("Nchat %s", messageBody);
                            command = extractCommand(messageBody, "N");
                        } /*
                            elif room.name in self.db.store['speak']:
                                # If speak mode is on
                                msg = 'Schat ' + msg
                                command = self.extract_command(msg, extra_prefixes='S')
                            elif (
                                isinstance(room.game, Game)
                                and room.game.__class__.__name__ not in user_data['restricted_games']
                                and not user_data['parked']
                                and not room.game.skip
                                and not room.sleep
                            ):
                                # games time
                                threading.Thread(
                                    target=room.game.action,
                                    args=(user, user_data, msg)
                                ).start()
                             */
                    }

                    if (null != command.get("name")
                            && !ArrayUtils.contains((String[]) userData.get("restrictedCommands"), command.get("name"))) {
                        new Command(command, room, realUserData, userData, realUser, user);
                    }
                } catch (Exception e) {
                    // Usually triggers when a command didn't finished properly
                    // Need to print some information about this error
                    System.out.println(String.format(
                            "Room %s: Exception thrown while trying to understand %s's message: %s",
                            room.getName(),
                            user.getName(),
                            messageBody
                    ));
                    e.printStackTrace();
                }
            }
        }
    }

    public void onFloodWarning(Room room) {
        System.out.println(String.format(
                "Room %s: Flood warned",
                room.getName()
        ));

        // TODO USE flood_warned_grace_period CONFIG
        room.setFlood(System.currentTimeMillis() + 100 * 1000);
        // TODO USE room.delay_when_flood_warned CONFIG
        room.setDelay(5 * 1000);  // set a rougher delay
        room.recalculateWaitUntil(); // for buffered messages
    }

    public void onFloodBan(Room room) {
        if (0 == room.getFlood()) {
            room.reconnect();
        }
    }

    public void onLoginFail(Room room) {
        room.setLoginFail(true);
    }

    public void onPMMessage(PM pm, User user, String messageBody) {
        // TODO
        new Thread() {
            @Override
            public void run() {
                pm.sendMessage(user, user.getCleverBot().chat(messageBody));
            }
        }.start();
    }

    public void onPMDisconnect(PM pm) {
        System.out.println(String.format("%s: disconnected", pm.getName()));
    }

    public void onPMOK(PM pm) {
        System.out.println(String.format("%s: connected", pm.getName()));
    }
}
