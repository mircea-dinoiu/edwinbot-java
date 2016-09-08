package repositories.room;

import common.repository.RepositoryItem;

public class RoomData extends RepositoryItem {
    private String name;
    private String initiator;
    private boolean blacklisted;
    private boolean sleeping;
    private String language;
    private String game;
    private int delay;
}
