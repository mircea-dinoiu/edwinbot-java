package manager.Bot;

import engine.chatango.common.Stream.Stream;
import engine.chatango.manager.EventManager.EventManager;
import org.apache.commons.lang3.ArrayUtils;

public class BotEvents extends BotStreamEvents implements EventManager {
    final private static String[] ON_EVENT_CALLED_IGNORE_EVENTS = {
        "raw", "ping", "PMPing", "userCountChange", "historyMessage"
    };

    public BotEvents(Object... args) throws Exception {
        super(args);
    }

    public void onEventCalled(Stream stream, String name, Object... args) {
        short logLevel = 2; // TODO GET LOG LEVEL FROM DATABASE

        if (logLevel > 1
                && !ArrayUtils.contains(ON_EVENT_CALLED_IGNORE_EVENTS, name)) {
            System.out.println(String.format("EventManager: Event %s was called", name));
        }
    }

    public void onInit() {
        // TODO SETUP THE LOGGER
        /*
        # Set up the logger
        self.logger = Log(self.db.get_config('log_directory'), 'commands', on_terminal=False)
         */

        setStyle();

        String[] roomNames = {"animecirclero"}; // TODO REPLACE WITH ROOMS METHOD FROM DATABASE BLA BLABLA

        for (String roomName : roomNames) {
            try {
                joinRoom(roomName);
            } catch (Exception e) {
                System.out.println(String.format("Room %s: Connection failed", roomName));
            }
        }

        startThreads();
    }

    public void onBeforeStop() {
//        # Remove "unused" chatrooms from the database
//        rooms = self.db.get_rooms(False)
//
//        for room in rooms:
    //        # Select unblock listed rooms
    //        room_data = self.db.get_room_data(room)
    //        # Avoid removing rooms that the bot is connected to or the default rooms
    //        if room not in self.get_room_names() and not room_data['default']:
    //              self.db.remove_room(room)
    }

    public void onAfterStop() {
//        # Save db store
//        root_path = self.db.get_config('root_path')
//
//        temp.save(self.db.store['bet_list'], 'bet_list', root_path)
//        temp.save(self.db.store['shapeshiftings'], 'shapeshiftings', root_path)
//        temp.save(self.db.store['redirects'], 'redirects', root_path)
//        temp.save(self.db.store['speak'], 'speak', root_path)
//        temp.save(self.db.store['vote_game'], 'vote_game', root_path)
//        temp.save(self.db.store['lotteries'], 'lotteries', root_path)
//        temp.save(self.db.store['cannons'], 'cannons', root_path)
    }


}
