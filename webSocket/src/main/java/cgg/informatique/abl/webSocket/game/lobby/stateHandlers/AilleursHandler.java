package cgg.informatique.abl.webSocket.game.lobby.stateHandlers;

import cgg.informatique.abl.webSocket.game.lobby.Lobby;
import cgg.informatique.abl.webSocket.game.lobby.LobbyPosition;
import cgg.informatique.abl.webSocket.game.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.game.lobby.LobbyUserData;

public class AilleursHandler extends LobbyRoleHandler {

    public AilleursHandler(Lobby context) {
        super(context);
    }

    @Override
    public LobbyPosition removeFromRole(LobbyUserData user) {
        getContext().removeAilleurs(user);
        return null;
    }

    @Override
    public LobbyPosition addToRole(LobbyUserData user, Integer position) {
        getContext().addAilleurs(user);
        return new LobbyPosition(LobbyRole.AILLEURS);
    }

    @Override
    public void checkAvailable(LobbyUserData user, Integer Position) {}
}
