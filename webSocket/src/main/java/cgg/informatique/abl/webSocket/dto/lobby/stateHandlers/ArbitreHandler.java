package cgg.informatique.abl.webSocket.dto.lobby.stateHandlers;

import cgg.informatique.abl.webSocket.dto.lobby.Lobby;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyPosition;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;

public class ArbitreHandler extends LobbyRoleHandler {
    public ArbitreHandler(Lobby context) {
        super(context);
    }

    @Override
    public LobbyPosition removeFromRole(LobbyUserData user) {
        getContext().setArbitre(null);
        return new LobbyPosition(LobbyRole.ARBITRE);
    }

    @Override
    public LobbyPosition addToRole(LobbyUserData user, Integer position) {
        getContext().setArbitre(user);
        return new LobbyPosition(LobbyRole.ARBITRE);
    }

    @Override
    public void checkAvailable(LobbyUserData user, Integer Position) {
        if (getContext().getArbitre() != null)
            throw new IllegalArgumentException("Il y a déjà un arbitre");
    }
}
