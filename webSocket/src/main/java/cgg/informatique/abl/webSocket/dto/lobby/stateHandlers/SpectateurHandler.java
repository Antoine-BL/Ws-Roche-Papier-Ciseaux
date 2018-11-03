package cgg.informatique.abl.webSocket.dto.lobby.stateHandlers;

import cgg.informatique.abl.webSocket.dto.lobby.Lobby;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyPosition;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;

public class SpectateurHandler extends LobbyRoleHandler {
    public SpectateurHandler(Lobby context) {
        super(context);
    }

    @Override
    public LobbyPosition removeFromRole(LobbyUserData user) {
        return getContext().getSpectateursColl().removeUser(user);
    }

    @Override
    public LobbyPosition addToRole(LobbyUserData user, Integer position) {
        if (position == null) {
            return getContext().getSpectateursColl().addUser(user);
        } else {
            return getContext().getSpectateursColl().addUserAt(user, position);
        }
    }

    @Override
    public void checkAvailable(LobbyUserData user, Integer Position) {
        getContext().getSpectateursColl().checkAvailable(Position);
    }
}
