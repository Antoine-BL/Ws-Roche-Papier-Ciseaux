package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Groupe;
import cgg.informatique.abl.webSocket.entites.Role;

import java.util.Objects;

public class LobbyUserData {
    private static int SECONDS = 1000;
    private static int MINUTES = 60 * SECONDS;
    private static int ACTIVE_TIMEOUT = 60 * MINUTES;
    private static int PASSIVE_TIMEOUT = 1 * MINUTES;
    private static int INACTIVE_THRESHOLD = 15 * SECONDS;
    private LobbyRole role;
    private SanitaryCompte user;
    private boolean warned = false;

    public LobbyUserData(SanitaryCompte user, LobbyRole role) {
        this.user = user;
        this.role = role;
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

    public SanitaryCompte getUser() {
        return user;
    }

    public void setUser(SanitaryCompte user) {
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

    public LobbyRole getRole() {
        return role;
    }

    public void setRole(LobbyRole role) {
        this.role = role;
    }

    public Role userRole() {
        return user.getRole();
    }

    public Groupe userGroup() {
        return user.getGroupe();
    }

    public long getLastActive() {
        return lastActive;
    }

    public void setLastActive(long lastActive) {
        this.lastActive = lastActive;
    }

    public long getLastPassive() {
        return lastPassive;
    }

    public void setLastPassive(long lastPassive) {
        this.lastPassive = lastPassive;
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
