package cgg.informatique.abl.webSocket.game.lobby;

import cgg.informatique.abl.webSocket.dto.UserBase;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Groupe;
import cgg.informatique.abl.webSocket.entites.Role;

public class LobbyUserData extends UserBase implements SanitizedLobbyUser {
    private static int SECONDS = 1000;
    private static int MINUTES = 60 * SECONDS;
    private static int ACTIVE_TIMEOUT = 15 * MINUTES;
    private static int PASSIVE_TIMEOUT = 5 * SECONDS;
    private static int INACTIVE_THRESHOLD = 15 * SECONDS;
    private int position = 0;
    private LobbyRole role;
    private Lobby lobby;
    private Compte user;
    private boolean warned = false;

    public LobbyUserData(Compte user, Lobby lobby) {
        this.user = user;
        this.lobby = lobby;
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

    public LobbyPosition leaveCurrentRole() {
        LobbyPosition pos = role.leaveRole(this);

        role = null;

        return pos;
    }

    public void becomeRole(LobbyPosition position) {
        LobbyPosition oldPos = null;
        if (role != null) {
            oldPos = leaveCurrentRole();
        }
        LobbyPosition newPos =  position.getRole().becomeRole(this, position);

        this.role = newPos.getRole();
        if (newPos.getPosition() != null) {
            this.position = newPos.getPosition();
        }

        lobby.posChanged(this, newPos, oldPos);
    }

    public int skillDeltaWith(LobbyUserData user) {
        int delta = user.getGroupe().getId() - this.getGroupe().getId();

        return Math.abs(delta);
    }

    public Compte getUser() {
        return user;
    }

    public void setUser(Compte user) {
        this.user = user;
    }

    public LobbyRole getRoleCombat() {
        return role;
    }

    public void setRole(LobbyRole role) {
        if (this.role != null) throw new IllegalStateException("Must leaveCurrentRole current role first");
        this.role = role;
    }

    @Override
    public Long getId() {
        return this.getUser().getId();
    }

    @Override
    public String getCourriel() {
        return this.user.getCourriel();
    }

    public String getAlias() {return user.getAlias(); }

    public Long getAvatarId() {return user.getAvatarId(); }

    @Override
    public void setAvatarId(Long id) {
        user.setAvatarId(id);
    }

    @Override
    public Role getRole() {
        return this.user.getRole();
    }

    @Override
    public Groupe getGroupe() {
        return this.user.getGroupe();
    }

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

    public Lobby getLobby() {
        return lobby;
    }

    public void checkCanGoTo(LobbyPosition position) {
        position
                .getRole()
                .checkAvailable(this, position.getPosition());
    }
}
