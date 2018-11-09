package cgg.informatique.abl.webSocket.game.lobby.stateHandlers;

import cgg.informatique.abl.webSocket.game.lobby.Lobby;
import cgg.informatique.abl.webSocket.game.lobby.LobbyPosition;
import cgg.informatique.abl.webSocket.game.lobby.LobbyUserData;

public class CombattantHandler extends LobbyRoleHandler {
    public CombattantHandler(Lobby context) {
        super(context);
    }

    @Override
    public LobbyPosition removeFromRole(LobbyUserData user) {
        return getContext().getCombattantsColl().removeUser(user);
    }

    @Override
    public LobbyPosition addToRole(LobbyUserData user, Integer position) {
        if (position == null) {
            return getContext().getCombattantsColl().addUser(user);
        } else {
            return getContext().getCombattantsColl().addUserAt(user, position);
        }
    }

    @Override
    public void checkAvailable(LobbyUserData user, Integer Position) {
        getContext().getCombattantsColl().checkAvailable(Position);
    }
}
