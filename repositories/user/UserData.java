package repositories.user;

import common.repository.RepositoryItem;

import java.util.Set;

public class UserData extends RepositoryItem {
    private String id;
    private String nick;
    private String language;
    private long bank;
    private long bankEarnings;
    private long coins;
    private boolean admin;
    private boolean ignored;
    private int level;
    private boolean parked;
    private long parkedUntil;
    private Set<String> restrictedCommands;
}
