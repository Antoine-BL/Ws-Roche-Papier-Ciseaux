package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Groupe;
import cgg.informatique.abl.webSocket.entites.Role;

import java.util.Objects;

public class LobbyUserData {
    private static long ACTIVE_TIMEOUT = 3 * 60 * 1000;
    private static long PASSIVE_TIMEOUT = 60 * 1000;
    private static long INACTIVE_THRESHOLD = 15 * 1000;
    private LobbyRole role;
    private Compte user;
    private boolean warned = false;

    public LobbyUserData(Compte user, LobbyRole role) {
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

    public Compte getUser() {
        return user;
    }

    public void setUser(Compte user) {
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
                Math.abs(PASSIVE_TIMEOUT - lastPassive) < PASSIVE_TIMEOUT;
    }

    public void setWarned(boolean warned) {
        this.warned = warned;
    }

    public boolean isWarned() {
        return warned;
    }
}
