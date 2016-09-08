package common;

import engine.chatango.stream.Room.Room;
import manager.Bot.Bot;
import manager.Language;

public class Game {
    protected String name;
    protected boolean error = false;
    protected boolean busy = false;
    protected String lastMessage;

    protected Room room;
    protected Bot bot;

    public Game(Room room) {
        this.room = room;
        this.bot = (Bot) room.getManager();
        this.name = Language.get(String.format("%s_GAME", getClass().getSimpleName()), room.getLanguage());
    }

    /**
     * Stops the game.
     * Sets the error flag to true and the Bot will unlink the Game from the Room.
     */
    public void stop() {
        error = true;
    }

    /**
     * Format coins.
     *
     * @param coins coins
     * @return formatted coins
     */
    public String formatCoins(Number coins) {
        return bot.formatCoins(coins, room.getLanguage());
    }

    public String getName() {
        return name;
    }
}
