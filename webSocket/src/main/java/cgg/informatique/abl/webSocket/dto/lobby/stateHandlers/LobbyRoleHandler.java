package cgg.informatique.abl.webSocket.dto.lobby.stateHandlers;

import cgg.informatique.abl.webSocket.dto.lobby.Lobby;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyPosition;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;

public abstract class LobbyRoleHandler {
    private Lobby context;

    public LobbyRoleHandler(Lobby context){
        this.context = context;
    }

    protected Lobby getContext() {
        return context;
    }

    public abstract LobbyPosition removeFromRole(LobbyUserData user);
    public abstract LobbyPosition addToRole(LobbyUserData user, Integer position);
    public abstract void checkAvailable(LobbyUserData user, Integer Position);
}
