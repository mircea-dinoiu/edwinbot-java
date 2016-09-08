package lib;

import com.google.code.chatterbotapi.ChatterBot;
import com.google.code.chatterbotapi.ChatterBotFactory;
import com.google.code.chatterbotapi.ChatterBotSession;
import com.google.code.chatterbotapi.ChatterBotType;

public class CleverBot {
    private ChatterBotSession session;

    public CleverBot() {
        createSession();
    }

    public String chat(String message) {
        String response = "";
        try {
            response = session.think(message).trim();

            if (0 == response.length()) {
                createSession();
                response = chat(message);
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private void createSession() {
        ChatterBotFactory factory = new ChatterBotFactory();

        try {
            ChatterBot bot = factory.create(ChatterBotType.CLEVERBOT);
            session = bot.createSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
