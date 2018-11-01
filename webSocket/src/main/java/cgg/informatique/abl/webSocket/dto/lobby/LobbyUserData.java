package cgg.informatique.abl.webSocket.dto.lobby;

import cgg.informatique.abl.webSocket.dto.SanitizedCompte;

import java.util.Objects;

public class LobbyUserData implements SanitizedCompte{
    private static int SECONDS = 1000;
    private static int MINUTES = 60 * SECONDS;
    private static int ACTIVE_TIMEOUT = 60 * MINUTES;
    private static int PASSIVE_TIMEOUT = 1 * MINUTES;
    private static int INACTIVE_THRESHOLD = 15 * SECONDS;
    private int position = 0;
    private LobbyRole role;
    private SanitizedCompte user;
    private boolean warned = false;

    public LobbyUserData(SanitizedCompte user, LobbyRole role) {
        this.user = user;
        this.role = role;
        this.position = position;
        lastPassive = System.currentTimeMillis();
        lastActive = System.currentTimeMillis();
    }

    private long lastActive;
    private long lastPassive;

    public boolean isTimedOut() {
        long currentTime = System.currentTimeMillis();
        return currentTime - lastActive >= ACTIVE_TIMEOUT || currentTime - lastPassive >= PASSIVE_TIMEOUT;
    }

    public void sentHeartbeat() {
        lastPassive = System.currentTimeMillis();
        this.warned = false;
    }

    public void sentCommand() {
        lastActive = System.currentTimeMillis();
        this.warned = false;
    }

    public SanitizedCompte getUser() {
        return user;
    }

    public void setUser(SanitizedCompte user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LobbyUserData that = (LobbyUserData) o;
        return lastActive == that.lastActive &&
                lastPassive == that.lastPassive &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, lastActive, lastPassive);
    }

    public LobbyRole getRoleCombat() {
        return role;
    }

    public void setRole(LobbyRole role) {
        this.role = role;
    }

    public String getRole() {
        return user.getRole();
    }

    public String getGroupe() {
        return user.getGroupe();
    }

    @Override
    public String getCourriel() {
        return null;
    }

    public String getAlias() {return user.getAlias(); }

    public Long getAvatarId() {return user.getAvatarId(); }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isInactive() {
        return Math.abs(ACTIVE_TIMEOUT - lastActive) < INACTIVE_THRESHOLD ||
                Math.abs(PASSIVE_TIMEOUT - lastPassive) < INACTIVE_THRESHOLD;
    }

    public void setWarned(boolean warned) {
        this.warned = warned;
    }

    public boolean isWarned() {
        return warned;
    }
}
